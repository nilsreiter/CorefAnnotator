package de.unistuttgart.ims.coref.annotator.plugins.io.quadrama;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;

public class QuaDramAPlugin implements IOPlugin {

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "QuaDramA";
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(ImportQuaDramA.class);
	}

	@Override
	public AnalysisEngineDescription getExporter() {
		return null;
	}

}
