package de.unistuttgart.ims.coref.annotator;

import static org.junit.Assert.assertEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.junit.Test;

public class TestUtil {
	@Test
	public void testRemoveFromStringArray() throws UIMAException {
		JCas jcas = JCasFactory.createJCas();
		StringArray arr, newArray;
		arr = new StringArray(jcas, 2);
		arr.set(0, "Hello");
		arr.set(1, "World");
		newArray = Util.removeFrom(jcas, arr, "Hello");

		assertEquals(1, newArray.size());
		assertEquals("World", newArray.get(0));
	}

	@Test
	public void testExtend() throws UIMAException {
		JCas jcas = JCasFactory.createText("The dog barks");
		Annotation a = AnnotationFactory.createAnnotation(jcas, 5, 6, Annotation.class);
		Util.extend(a);
		assertEquals(4, a.getBegin());

		a = AnnotationFactory.createAnnotation(jcas, 0, 3, Annotation.class);
		Util.extend(a);
		assertEquals(0, a.getBegin());
		assertEquals(3, a.getEnd());
		assertEquals("The", a.getCoveredText());

		a = AnnotationFactory.createAnnotation(jcas, 0, 2, Annotation.class);
		Util.extend(a);
		assertEquals(0, a.getBegin());
		assertEquals(3, a.getEnd());

		a = AnnotationFactory.createAnnotation(jcas, 1, 2, Annotation.class);
		Util.extend(a);
		assertEquals(0, a.getBegin());
		assertEquals(3, a.getEnd());

		a = AnnotationFactory.createAnnotation(jcas, 10, 11, Annotation.class);
		Util.extend(a);
		assertEquals(8, a.getBegin());
		assertEquals(13, a.getEnd());
		assertEquals("barks", a.getCoveredText());

		a = AnnotationFactory.createAnnotation(jcas, 8, 13, Annotation.class);
		Util.extend(a);
		assertEquals(8, a.getBegin());
		assertEquals(13, a.getEnd());

		a = AnnotationFactory.createAnnotation(jcas, 12, 13, Annotation.class);
		Util.extend(a);
		assertEquals(8, a.getBegin());
		assertEquals(13, a.getEnd());

		a = AnnotationFactory.createAnnotation(jcas, 8, 9, Annotation.class);
		Util.extend(a);
		assertEquals(8, a.getBegin());
		assertEquals(13, a.getEnd());

	}

	@Test
	public void misc() {
		System.out.println(String.format("%1$,3d (%2$3.1f%%)", 2342, 0.35 * 100));
	}
}
