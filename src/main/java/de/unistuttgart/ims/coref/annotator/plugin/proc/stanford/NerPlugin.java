package de.unistuttgart.ims.coref.annotator.plugin.proc.stanford;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;
import de.unistuttgart.ims.coref.annotator.plugins.ProcessingPlugin;

public class NerPlugin implements ProcessingPlugin {

	@Override
	public String getDescription() {
		return "Stanford Named Entity Recognition ";
	}

	@Override
	public String getName() {
		return "Stanford NER";
	}

	@Override
	public AnalysisEngineDescription getEngineDescription() throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(StanfordSegmenter.class));
		b.add(AnalysisEngineFactory.createEngineDescription(StanfordNamedEntityRecognizer.class));
		// b.add(AnalysisEngineFactory.createEngineDescription(ClearAnnotation.class,
		// ClearAnnotation.PARAM_TYPE));
		return b.createAggregateDescription();
	}

	@Override
	public String[] getSupportedLanguages() {
		return new String[] { "en" };
	}

}
