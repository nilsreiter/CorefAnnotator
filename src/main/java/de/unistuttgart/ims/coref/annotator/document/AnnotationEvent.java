package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.jcas.tcas.Annotation;

public class AnnotationEvent implements Event {
	Type type;
	Annotation annotation;

	public AnnotationEvent(Type type, Annotation annotation) {
		this.type = type;
		this.annotation = annotation;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}
}
