package de.unistuttgart.ims.coref.annotator.plugins.dkpro;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;

public class DKproPlugin implements IOPlugin {

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getName() {
		return "DKpro";
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(ImportDKpro.class);
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		return null;
	}

	@Override
	public StylePlugin getStylePlugin() {
		return null;
	}

}
