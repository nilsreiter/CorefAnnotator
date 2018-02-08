package de.unistuttgart.ims.coref.annotator.plugin.creta.adorno;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.plugins.DefaultIOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;

public class Plugin extends DefaultIOPlugin {

	@Override
	public String getDescription() {
		return "Importer for Adorno annotations done in CRETAnno";
	}

	@Override
	public String getName() {
		return "CRETA/Adorno";
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(Importer.class);
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(Exporter.class);
	}

	@Override
	public StylePlugin getStylePlugin() {
		return null;
	}

}
