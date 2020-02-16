package de.unistuttgart.ims.coref.annotator.plugin.proc.breakiterator;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.kordamp.ikonli.Ikon;

import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.plugins.ProcessingPlugin;

public class Tokenizer implements ProcessingPlugin {

	@Override
	public String getDescription() {
		return "A simple rule-based tokenizer";
	}

	@Override
	public String getName() {
		return "BreakIterator";
	}

	@Override
	public Ikon getIkon() {
		return null;
	}

	@Override
	public AnalysisEngineDescription getEngineDescription() throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(BreakIteratorSegmenter.class));
		return b.createAggregateDescription();
	}

	@Override
	public String[] getSupportedLanguages() {
		return Constants.SUPPORTED_LANGUAGES;
	}

}
