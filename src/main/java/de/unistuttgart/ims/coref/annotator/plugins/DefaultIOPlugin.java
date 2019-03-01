package de.unistuttgart.ims.coref.annotator.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.swing.filechooser.FileFilter;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.unistuttgart.ims.coref.annotator.ExtensionFilters;
import de.unistuttgart.ims.coref.annotator.FileFilters;
import javafx.stage.FileChooser.ExtensionFilter;

public final class DefaultIOPlugin extends AbstractXmiPlugin implements DirectFileIOPlugin {

	File lastFile;

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "Default";
	}

	@Override
	public CollectionReaderDescription getReader(File f) throws ResourceInitializationException {
		lastFile = f;
		return CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_LENIENT, true,
				XmiReader.PARAM_ADD_DOCUMENT_METADATA, false, XmiReader.PARAM_SOURCE_LOCATION, f.getAbsolutePath());
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		AggregateBuilder b1 = new AggregateBuilder();
		b1.add(AnalysisEngineFactory.createEngineDescription(CheckLoadability.class));
		return b1.createAggregateDescription();
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(NoOpAnnotator.class);
	}

	@Override
	public Class<? extends StylePlugin> getStylePlugin() {
		return null;
	}

	@Override
	public FileFilter getFileFilter() {
		return FileFilters.xmi_gz;
	}

	@Override
	public String[] getSupportedLanguages() {
		return de.unistuttgart.ims.coref.annotator.Constants.SUPPORTED_LANGUAGES;
	}

	@Override
	public JCas getJCas(File f) throws IOException, UIMAException {
		InputStream is = null;
		try {
			if (f.getName().endsWith(".xmi")) {
				is = new FileInputStream(f);
			} else if (f.getName().endsWith(".xmi.gz")) {
				is = new GZIPInputStream(new FileInputStream(f));
			}

			JCas jcas = JCasFactory.createJCas();
			XmiCasDeserializer.deserialize(is, jcas.getCas());
			return jcas;
		} catch (SAXException e) {
			throw new IOException(e);
		} finally {
			if (is != null)
				is.close();
		}
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return ExtensionFilters.xmi_gz;
	}

}
