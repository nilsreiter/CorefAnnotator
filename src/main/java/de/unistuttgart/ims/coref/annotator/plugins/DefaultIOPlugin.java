package de.unistuttgart.ims.coref.annotator.plugins;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

public class DefaultIOPlugin implements IOPlugin {

	static DefaultIOPlugin plugin = new DefaultIOPlugin();

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "Default";
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(NoOpAnnotator.class);
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(NoOpAnnotator.class);
	}

	public static DefaultIOPlugin getInstance() {
		return plugin;
	}

}
