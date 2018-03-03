package de.unistuttgart.ims.coref.annotator.plugin.auto.stanford;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordCoreferenceResolver;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;
import de.unistuttgart.ims.coref.annotator.plugin.dkpro.ImportDKpro;
import de.unistuttgart.ims.coref.annotator.plugins.AutomaticCRPlugin;

public class StanfordCoref implements AutomaticCRPlugin {

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "Stanford Coreference Resolver";
	}

	@Override
	public AnalysisEngineDescription getEngineDescription() {
		try {
			AggregateBuilder b = new AggregateBuilder();
			b.add(AnalysisEngineFactory.createEngineDescription(StanfordSegmenter.class));
			b.add(AnalysisEngineFactory.createEngineDescription(StanfordPosTagger.class));
			b.add(AnalysisEngineFactory.createEngineDescription(StanfordParser.class));
			b.add(AnalysisEngineFactory.createEngineDescription(StanfordCoreferenceResolver.class,
					StanfordCoreferenceResolver.PARAM_SINGLETON, true));
			b.add(AnalysisEngineFactory.createEngineDescription(ImportDKpro.class));
			return b.createAggregateDescription();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String[] getSupportedLanguages() {
		return new String[] { "en" };
	}

}
