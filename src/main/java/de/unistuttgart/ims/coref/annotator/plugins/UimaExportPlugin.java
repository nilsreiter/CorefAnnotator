package de.unistuttgart.ims.coref.annotator.plugins;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

public interface UimaExportPlugin extends ExportPlugin {
	AnalysisEngineDescription getExporter() throws ResourceInitializationException;

	AnalysisEngineDescription getWriter(File f) throws ResourceInitializationException;

}
