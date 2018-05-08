package de.unistuttgart.ims.coref.annotator.plugin.creta.webanno;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.plugins.AbstractXmiPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;

public class CRETAPlugin extends AbstractXmiPlugin {

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getName() {
		return "CRETA/WebAnno";
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(ImportCRETA.class);
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		return null;
	}

	@Override
	public Class<? extends StylePlugin> getStylePlugin() {
		return null;
	}

}
