package de.unistuttgart.ims.coref.annotator.plugin.dkpro;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.plugins.AbstractXmiPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;
import de.unistuttgart.ims.coref.annotator.uima.EnsureMeta;

/**
 * TODO: Compile without access to java classes
 *
 */
public class DKproPlugin extends AbstractXmiPlugin {

	@Override
	public String getDescription() {
		return "Imports coreference annotations in the DKpro type system (also builtin to WebAnno)";
	}

	@Override
	public String getName() {
		return "DKpro";
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(ImportDKpro.class));
		b.add(AnalysisEngineFactory.createEngineDescription(EnsureMeta.class));
		return b.createAggregateDescription();
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
