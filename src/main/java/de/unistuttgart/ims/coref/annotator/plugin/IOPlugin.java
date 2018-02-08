package de.unistuttgart.ims.coref.annotator.plugin;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

public interface IOPlugin {
	String getDescription();

	String getName();

	AnalysisEngineDescription getImporter() throws ResourceInitializationException;

	AnalysisEngineDescription getExporter() throws ResourceInitializationException;

	StylePlugin getStylePlugin();

}
