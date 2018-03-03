package de.unistuttgart.ims.coref.annotator.plugins;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;

public interface AutomaticCRPlugin extends Plugin {
	AnalysisEngineDescription getEngineDescription();

	String[] getSupportedLanguages();
}
