package de.unistuttgart.ims.coref.annotator.uima;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.uima.cas.CASException;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.api.v2.MentionSurface;

public class TestUimaUtil {

	JCas jcas;

	@Before
	public void setUp() throws ResourceInitializationException, CASException {
		jcas = JCasFactory.createText("abcdefghijklmnopqrstuvxyz");
	}

	@Test
	public void testRemoveMentionSurface() {
		MentionSurface[] ms = new MentionSurface[] {
				AnnotationFactory.createAnnotation(jcas, 0, 2, MentionSurface.class),
				AnnotationFactory.createAnnotation(jcas, 3, 5, MentionSurface.class),
				AnnotationFactory.createAnnotation(jcas, 6, 8, MentionSurface.class) };

		Mention m = new Mention(jcas);
		m.addToIndexes();
		m.setSurface(new FSArray<MentionSurface>(jcas, 3));
		m.setSurface(0, ms[0]);
		m.setSurface(1, ms[1]);
		m.setSurface(2, ms[2]);

		assertEquals(3, m.getSurface().size());

		UimaUtil.removeMentionSurface(m, ms[1]);

		assertEquals(2, m.getSurface().size());
		assertEquals(0, m.getSurface(0).getBegin());
		assertEquals(6, m.getSurface(1).getBegin());

		UimaUtil.removeMentionSurface(m, ms[0]);

		assertEquals(1, m.getSurface().size());
		assertEquals(6, m.getSurface(0).getBegin());

	}

	@Test
	public void testAddMentionSurface() {
		Mention m = new Mention(jcas);
		m.addToIndexes();
		m.setSurface(new FSArray<MentionSurface>(jcas, 0));

		MentionSurface[] ms = new MentionSurface[] {
				AnnotationFactory.createAnnotation(jcas, 0, 2, MentionSurface.class),
				AnnotationFactory.createAnnotation(jcas, 5, 6, MentionSurface.class),
				AnnotationFactory.createAnnotation(jcas, 10, 12, MentionSurface.class) };

		UimaUtil.addMentionSurface(m, ms[0]);

		assertNotNull(m.getSurface());
		assertEquals(1, m.getSurface().size());

		UimaUtil.addMentionSurface(m, ms[2]);

		assertNotNull(m.getSurface());
		assertEquals(2, m.getSurface().size());
		assertEquals(0, m.getSurface(0).getBegin());
		assertEquals(10, m.getSurface(1).getBegin());

		UimaUtil.addMentionSurface(m, ms[1]);

		assertNotNull(m.getSurface());
		assertEquals(3, m.getSurface().size());
		assertEquals(0, m.getSurface(0).getBegin());
		assertEquals(5, m.getSurface(1).getBegin());
		assertEquals(10, m.getSurface(2).getBegin());

	}
}
