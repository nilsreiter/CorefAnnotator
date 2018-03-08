package de.unistuttgart.ims.coref.annotator.uima;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.coref.annotator.api.Entity;

public class TestCoNLL2012Reader {

	@Test
	public void testReader() throws ResourceInitializationException {
		CollectionReaderDescription readerDesc = CollectionReaderFactory.createReaderDescription(CoNLL2012Reader.class,
				CoNLL2012Reader.PARAM_SOURCE_LOCATION,
				"src/test/resources/conll/2012/test-doc-tueba-gold-parses.conll");

		JCas jcas = SimplePipeline
				.iteratePipeline(readerDesc, AnalysisEngineFactory.createEngineDescription(NoOpAnnotator.class))
				.iterator().next();

		assertNotNull(jcas);
		assertTrue(JCasUtil.exists(jcas, Sentence.class));
		assertTrue(JCasUtil.exists(jcas, Token.class));
		assertEquals("Disziplinarverfahren", JCasUtil.selectByIndex(jcas, Sentence.class, 0).getCoveredText());
		assertEquals("Stadtrat \" traute \" homosexuelle Paare",
				JCasUtil.selectByIndex(jcas, Sentence.class, 1).getCoveredText());
		assertEquals(2, JCasUtil.select(jcas, Entity.class).size());
	}
}
