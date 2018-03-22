package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.jcas.tcas.Annotation;

public class AnnotationEvent<T extends Annotation> implements Event {
	Type type;
	T annotation;

	public AnnotationEvent(Type type, T annotation) {
		this.type = type;
		this.annotation = annotation;
	}

	@Override
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public T getAnnotation() {
		return annotation;
	}

	public void setAnnotation(T annotation) {
		this.annotation = annotation;
	}

	@Override
	public Op getOp() {
		return null;
	}
}
