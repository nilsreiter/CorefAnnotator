package de.unistuttgart.ims.coref.annotator.plugin.tei;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.uima.io.xml.type.XMLElement;

public class TestMapCorefToXmlElements {
	JCas jcas;
	Entity entity, entity2;

	@Before
	public void setUp() throws UIMAException {
		jcas = JCasFactory.createText("the dog barks");
		entity = new Entity(jcas);
		entity.addToIndexes();
		Mention m = AnnotationFactory.createAnnotation(jcas, 0, 1, Mention.class);
		m.setEntity(entity);
		m = AnnotationFactory.createAnnotation(jcas, 1, 2, Mention.class);
		m.setEntity(entity);

		entity2 = new Entity(jcas);
		entity2.addToIndexes();
		AnnotationFactory.createAnnotation(jcas, 2, 3, Mention.class).setEntity(entity2);
		AnnotationFactory.createAnnotation(jcas, 3, 4, Mention.class).setEntity(entity2);

	}

	@Test
	public void testGeneratedIdFromScratch() throws AnalysisEngineProcessException, ResourceInitializationException {
		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertTrue(JCasUtil.exists(jcas, Entity.class));
		assertFalse(JCasUtil.exists(jcas, XMLElement.class));

		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory.createEngineDescription(MapCorefToXmlElements.class));

		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertTrue(JCasUtil.exists(jcas, Entity.class));
		assertTrue(JCasUtil.exists(jcas, XMLElement.class));

		XMLElement e = JCasUtil.selectByIndex(jcas, XMLElement.class, 0);

		assertEquals("rs", e.getTag());
		assertEquals(0, e.getBegin());
		assertEquals(1, e.getEnd());
		assertEquals(" xml:id=\"e1\"", e.getAttributes());

		e = JCasUtil.selectByIndex(jcas, XMLElement.class, 1);

		assertEquals("rs", e.getTag());
		assertEquals(1, e.getBegin());
		assertEquals(2, e.getEnd());
		assertEquals(" ref=\"#e1\"", e.getAttributes());

		e = JCasUtil.selectByIndex(jcas, XMLElement.class, 2);
		assertEquals("rs", e.getTag());
		assertEquals(" xml:id=\"e2\"", e.getAttributes());

		e = JCasUtil.selectByIndex(jcas, XMLElement.class, 3);
		assertEquals("rs", e.getTag());
		assertEquals(" ref=\"#e2\"", e.getAttributes());
	}

	@Test
	public void testGeneratedIdFromLabel() throws AnalysisEngineProcessException, ResourceInitializationException {
		entity.setLabel("dog");
		entity2.setLabel("dog");
		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory.createEngineDescription(MapCorefToXmlElements.class));

		XMLElement e = JCasUtil.selectByIndex(jcas, XMLElement.class, 0);

		assertEquals("rs", e.getTag());
		assertEquals(0, e.getBegin());
		assertEquals(1, e.getEnd());
		assertEquals(" xml:id=\"dog\"", e.getAttributes());

		e = JCasUtil.selectByIndex(jcas, XMLElement.class, 1);

		assertEquals("rs", e.getTag());
		assertEquals(1, e.getBegin());
		assertEquals(2, e.getEnd());
		assertEquals(" ref=\"#dog\"", e.getAttributes());

		e = JCasUtil.selectByIndex(jcas, XMLElement.class, 2);
		assertEquals("rs", e.getTag());
		assertEquals(" xml:id=\"dog2\"", e.getAttributes());

		e = JCasUtil.selectByIndex(jcas, XMLElement.class, 3);
		assertEquals("rs", e.getTag());
		assertEquals(" ref=\"#dog2\"", e.getAttributes());

	}

	@Test
	public void testExistingId() throws AnalysisEngineProcessException, ResourceInitializationException {
		entity.setXmlId("dog");
		entity2.setXmlId("dog");
		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory.createEngineDescription(MapCorefToXmlElements.class));

		XMLElement e = JCasUtil.selectByIndex(jcas, XMLElement.class, 0);

		assertEquals("rs", e.getTag());
		assertEquals(0, e.getBegin());
		assertEquals(1, e.getEnd());
		assertEquals(" xml:id=\"dog\"", e.getAttributes());

		e = JCasUtil.selectByIndex(jcas, XMLElement.class, 1);

		assertEquals("rs", e.getTag());
		assertEquals(1, e.getBegin());
		assertEquals(2, e.getEnd());
		assertEquals(" ref=\"#dog\"", e.getAttributes());

		e = JCasUtil.selectByIndex(jcas, XMLElement.class, 2);
		assertEquals("rs", e.getTag());
		assertEquals(" ref=\"#dog\"", e.getAttributes());

		e = JCasUtil.selectByIndex(jcas, XMLElement.class, 3);
		assertEquals("rs", e.getTag());
		assertEquals(" ref=\"#dog\"", e.getAttributes());
	}
}
