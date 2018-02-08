package de.unistuttgart.ims.coref.annotator.plugins.creta.webanno;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.plugin.IOPlugin;
import de.unistuttgart.ims.coref.annotator.plugin.StylePlugin;

public class CRETAPlugin implements IOPlugin {

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
	public StylePlugin getStylePlugin() {
		return null;
	}

}
