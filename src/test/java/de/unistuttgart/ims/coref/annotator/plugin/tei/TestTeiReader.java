package de.unistuttgart.ims.coref.annotator.plugin.tei;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class TestTeiReader {
	@Test
	public void testReader() throws UIMAException, IOException {
		CollectionReaderDescription crd = CollectionReaderFactory.createReaderDescription(TeiReader.class,
				TeiReader.PARAM_SOURCE_LOCATION, "src/test/resources/tei/test.xml");

		JCasIterable iterable = SimplePipeline.iteratePipeline(crd,
				AnalysisEngineFactory.createEngineDescription(NoOpAnnotator.class));
		JCas jcas = iterable.iterator().next();

		assertEquals(2, JCasUtil.select(jcas, Mention.class).size());
		assertEquals(1, JCasUtil.select(jcas, Entity.class).size());
	}
}
