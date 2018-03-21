package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.jcas.tcas.Annotation;

public class AnnotationMoveEvent<T extends Annotation> extends AnnotationEvent<T> {

	Object from;
	Object to;

	public AnnotationMoveEvent(T annotation, Object from, Object to) {
		super(Type.Move, annotation);
		this.from = from;
		this.to = to;
	}

	public Object getFrom() {
		return from;
	}

	public void setFrom(Object from) {
		this.from = from;
	}

	public Object getTo() {
		return to;
	}

	public void setTo(Object to) {
		this.to = to;
	}

}
