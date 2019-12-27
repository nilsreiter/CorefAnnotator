package de.unistuttgart.ims.coref.annotator.plugins;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ResourceInitializationException;

public interface UimaImportPlugin extends ImportPlugin {
	AnalysisEngineDescription getImporter() throws ResourceInitializationException;

	CollectionReaderDescription getReader(File f) throws ResourceInitializationException;

}
