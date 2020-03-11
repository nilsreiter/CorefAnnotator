package de.unistuttgart.ims.coref.annotator.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;

import javax.swing.filechooser.FileFilter;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.coref.annotator.ExtensionFilters;
import de.unistuttgart.ims.coref.annotator.FileFilters;
import de.unistuttgart.ims.uimautil.SetDocumentId;
import javafx.stage.FileChooser.ExtensionFilter;

public final class DefaultExportPlugin extends AbstractExportPlugin implements DirectFileIOPlugin, UimaExportPlugin {

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
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(NoOpAnnotator.class);
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

	@Override
	public Ikon getIkon() {
		return MaterialDesign.MDI_FILE_EXPORT;
	}

	@Override
	public Consumer<File> getPostExportAction() {
		return null;
	}

	@Override
	public String getSuffix() {
		return ".xmi";
	}

	@Override
	public AnalysisEngineDescription getWriter(File f) throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(SetDocumentId.class, SetDocumentId.PARAM_DOCUMENT_ID,
				f.getName().replaceAll(getSuffix(), "")));
		b.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
				f.getParentFile().getAbsolutePath(), XmiWriter.PARAM_USE_DOCUMENT_ID, true, XmiWriter.PARAM_OVERWRITE,
				true));
		return b.createAggregateDescription();
	}
}
