package de.unistuttgart.ims.coref.annotator.plugin.creta.adorno;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.plugins.AbstractXmiPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;
import de.unistuttgart.ims.uimautil.ClearAnnotation;

public class Plugin extends AbstractXmiPlugin {

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
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class, ClearAnnotation.PARAM_TYPE,
				Constants.mentionTypeName));
		b.add(AnalysisEngineFactory.createEngineDescription(Exporter.class));
		return b.createAggregateDescription();
	}

	@Override
	public Class<? extends StylePlugin> getStylePlugin() {
		return null;
	}

}
