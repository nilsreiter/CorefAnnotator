package de.unistuttgart.ims.coref.annotator.worker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javax.swing.SwingWorker;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.pipeline.JCasIterator;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.xml.sax.SAXException;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.uima.EnsureMeta;
import de.unistuttgart.ims.uimautil.SetJCasLanguage;

public class JCasLoader extends SwingWorker<JCas, Object> {

	@Deprecated
	DocumentWindow documentWindow;
	InputStream inputStream = null;
	TypeSystemDescription typeSystemDescription;
	IOPlugin flavor;
	File file = null;
	String language = null;
	Consumer<JCas> consumer = null;

	@Deprecated
	public JCasLoader(DocumentWindow documentWindow, InputStream inputStream,
			TypeSystemDescription typeSystemDescription, IOPlugin flavor, String language) {
		this.documentWindow = documentWindow;
		this.inputStream = inputStream;
		this.typeSystemDescription = typeSystemDescription;
		this.flavor = flavor;
		this.language = language;
	}

	@Deprecated
	public JCasLoader(DocumentWindow documentWindow, File file, TypeSystemDescription typeSystemDescription,
			IOPlugin flavor, String language) {
		this.documentWindow = documentWindow;
		this.typeSystemDescription = typeSystemDescription;
		this.flavor = flavor;
		this.file = file;
		this.language = language;
	}

	public JCasLoader(Consumer<JCas> consumer, File file, TypeSystemDescription typeSystemDescription, IOPlugin flavor,
			String language) {
		this.consumer = consumer;
		this.typeSystemDescription = typeSystemDescription;
		this.flavor = flavor;
		this.file = file;
		this.language = language;
	}

	public JCasLoader(Consumer<JCas> consumer, File file) {
		this.consumer = consumer;
		try {
			this.typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		this.flavor = Annotator.app.getPluginManager().getDefaultIOPlugin();
		this.file = file;
		this.language = "de";
	}

	@Deprecated
	private JCas readStream() {
		JCas jcas = null;

		try {
			jcas = JCasFactory.createJCas(typeSystemDescription);
			jcas.setDocumentText(language);
		} catch (UIMAException e1) {
			Annotator.logger.catching(e1);
			return null;
		}
		try {
			Annotator.logger.info("Deserialising input stream.");
			XmiCasDeserializer.deserialize(inputStream, jcas.getCas(), true);

		} catch (SAXException | IOException e1) {
			Annotator.logger.catching(e1);
		}
		try {
			Annotator.logger.info("Applying importer from {}", flavor.getClass().getName());
			SimplePipeline.runPipeline(jcas, flavor.getImporter(),
					AnalysisEngineFactory.createEngineDescription(EnsureMeta.class));

		} catch (AnalysisEngineProcessException | ResourceInitializationException e1) {
			Annotator.logger.catching(e1);
		}

		return jcas;
	}

	private JCas readFile() throws ResourceInitializationException {
		JCasIterator iter;
		AggregateBuilder b = new AggregateBuilder();
		if (getLanguage() != null)
			b.add(AnalysisEngineFactory.createEngineDescription(SetJCasLanguage.class, SetJCasLanguage.PARAM_LANGUAGE,
					getLanguage()));
		b.add(flavor.getImporter());

		iter = SimplePipeline.iteratePipeline(flavor.getReader(file), b.createAggregateDescription()).iterator();
		if (iter.hasNext()) {
			return iter.next();
		}
		return null;

	}

	@Override
	protected JCas doInBackground() throws Exception {

		if (inputStream != null)
			return readStream();
		else if (file != null)
			return readFile();
		return null;
	}

	@Override
	protected void done() {
		try {
			this.consumer.accept(get());
		} catch (InterruptedException | ExecutionException e) {
			Annotator.logger.catching(e);
		}
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}