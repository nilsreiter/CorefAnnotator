package de.unistuttgart.ims.coref.annotator.plugin.versions.legacy;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.plugins.AbstractXmiPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;

public class Plugin extends AbstractXmiPlugin {

	@Override
	public String getDescription() {
		return "Import LEGACY files and convert them to file format 1.0";
	}

	@Override
	public String getName() {
		return "LEGACY";
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(LEGACY_To_V1_0.class));
		return b.createAggregateDescription();
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		return null;
	}

	@Override
	public AnalysisEngineDescription getWriter(File f) throws ResourceInitializationException {
		return null;
	}

	@Override
	public Class<? extends StylePlugin> getStylePlugin() {
		return null;
	}

}
