package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.jcas.tcas.Annotation;

public class AnnotationEvent<T extends Annotation> extends FeatureStructureEvent<T> {

	public AnnotationEvent(Type type, T annotation) {
		super(type, annotation);
	}

}
