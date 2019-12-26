package de.unistuttgart.ims.coref.annotator.plugins;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ResourceInitializationException;

public interface UimaIOPlugin extends IOPlugin {

	AnalysisEngineDescription getImporter() throws ResourceInitializationException;

	AnalysisEngineDescription getExporter() throws ResourceInitializationException;

	CollectionReaderDescription getReader(File f) throws ResourceInitializationException;

	AnalysisEngineDescription getWriter(File f) throws ResourceInitializationException;

}
