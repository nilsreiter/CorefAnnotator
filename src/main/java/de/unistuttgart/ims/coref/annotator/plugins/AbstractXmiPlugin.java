package de.unistuttgart.ims.coref.annotator.plugins;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.coref.annotator.FileFilters;
import de.unistuttgart.ims.uimautil.SetDocumentId;

public abstract class AbstractXmiPlugin implements IOPlugin {

	@Override
	public FileFilter getFileFilter() {
		return FileFilters.xmi;
	}

	@Override
	public CollectionReaderDescription getReader(File f) throws ResourceInitializationException {
		return CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_LENIENT, true,
				XmiReader.PARAM_ADD_DOCUMENT_METADATA, false, XmiReader.PARAM_SOURCE_LOCATION, f.getAbsolutePath());
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

	@Override
	public String getSuffix() {
		return ".xmi";
	}

	@Override
	public String[] getSupportedLanguages() {
		return de.unistuttgart.ims.coref.annotator.Constants.SUPPORTED_LANGUAGES;
}
}
