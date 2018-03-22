package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.jcas.tcas.Annotation;

public class AnnotationEvent extends FeatureStructureEvent {

	public AnnotationEvent(Type type, Annotation annotation) {
		super(type, annotation);
	}

}
