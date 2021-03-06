package de.unistuttgart.ims.coref.annotator.plugin.auto.stanford;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.io.text.TextReader;
import org.dkpro.core.stanfordnlp.StanfordCoreferenceResolver;
import org.dkpro.core.stanfordnlp.StanfordParser;
import org.dkpro.core.stanfordnlp.StanfordPosTagger;
import org.dkpro.core.stanfordnlp.StanfordSegmenter;

import de.unistuttgart.ims.coref.annotator.plugin.dkpro.ImportDKpro;
import de.unistuttgart.ims.coref.annotator.uima.SetJCasLanguage;

public class StanfordCoref extends de.unistuttgart.ims.coref.annotator.plugin.plaintext.Plugin {

	@Override
	public String getDescription() {
		return "Import (English) plain text and run it through the Stanford pipeline, including Coreference";
	}

	@Override
	public String getName() {
		return "Stanford CR";
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		try {
			AggregateBuilder b = new AggregateBuilder();
			b.add(AnalysisEngineFactory.createEngineDescription(SetJCasLanguage.class, SetJCasLanguage.PARAM_LANGUAGE,
					"en"));
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
	public CollectionReaderDescription getReader(File f) throws ResourceInitializationException {
		return CollectionReaderFactory.createReaderDescription(TextReader.class, TextReader.PARAM_SOURCE_LOCATION,
				f.getAbsolutePath(), TextReader.ENCODING_AUTO, true, TextReader.PARAM_LANGUAGE, "en");
	}

	@Override
	public String[] getSupportedLanguages() {
		return new String[] { "en" };
	}
}
