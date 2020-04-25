package de.unistuttgart.ims.coref.annotator.plugin.b06.b03;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.coref.annotator.api.v1.Segment;

public class TestTeiReader {

	@Test
	public void testTeiReader() throws ResourceInitializationException {

		JCas jcas = SimplePipeline.iteratePipeline(
				CollectionReaderFactory.createReaderDescription(TeiReader.class, TeiReader.PARAM_SOURCE_LOCATION,
						"sfb1391/test/resources/*.xml", TeiReader.PARAM_DOCUMENT_ID, "Rumelant"),
				AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
						"target"))
				.iterator().next();

		assertNotNull(jcas);

		Segment segment;
		segment = JCasUtil.selectByIndex(jcas, Segment.class, 112);

		assertEquals(12, segment.getBegin());
	}
}
