package de.unistuttgart.ims.coref.annotator.uima;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

public interface DiscontinuousAnnotation<A extends Annotation, F extends FeatureStructure> {
	int getBegin();

	int getEnd();

	FSArray<A> getSurface();

	A getSurface(int i);

	A getFirst();

	A getLast();

	String getCoveredText();
}
