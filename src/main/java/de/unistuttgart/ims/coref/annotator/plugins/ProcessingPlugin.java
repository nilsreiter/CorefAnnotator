package de.unistuttgart.ims.coref.annotator.plugins;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

public interface ProcessingPlugin extends Plugin {
	AnalysisEngineDescription getEngineDescription() throws ResourceInitializationException;

	String[] getSupportedLanguages();

}
