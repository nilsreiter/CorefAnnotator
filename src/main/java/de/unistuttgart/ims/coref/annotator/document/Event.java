package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.tcas.Annotation;

public interface Event {
	public enum Type {
		Add, Remove, Update, Move
	};

	Type getType();

	public static AnnotationEvent get(Type type, Annotation annotation) {
		return new AnnotationEvent(type, annotation);
	}

	public static AnnotationEvent get(Annotation annotation, Object from, Object to) {
		return new AnnotationMoveEvent(annotation, from, to);
	}

	public static FeatureStructureEvent get(Type type, FeatureStructure fs) {
		return new FeatureStructureEvent(type, fs);
	}

}
