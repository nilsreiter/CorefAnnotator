package de.unistuttgart.ims.coref.annotator.uima;

import java.util.Comparator;

import org.apache.uima.jcas.tcas.Annotation;

public class AnnotationLengthComparator<T extends Annotation> implements Comparator<T> {

	@Override
	public int compare(T o1, T o2) {
		return Integer.compare(o1.getEnd() - o1.getBegin(), o2.getEnd() - o2.getBegin());
	}

}
