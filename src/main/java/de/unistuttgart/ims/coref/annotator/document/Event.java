package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.Entity;

public interface Event {
	public enum Type {
		Add, Remove, Update, Move, Merge
	};

	Type getType();

	public static <T extends Annotation> AnnotationEvent<T> get(Type type, T annotation) {
		return new AnnotationEvent<T>(type, annotation);
	}

	public static <T extends Annotation> AnnotationEvent<T> get(T annotation, Object from, Object to) {
		return new AnnotationMoveEvent<T>(annotation, from, to);
	}

	public static FeatureStructureEvent<Entity> get(Type type, Entity fs) {
		return new FeatureStructureEvent<Entity>(type, fs);
	}

}
