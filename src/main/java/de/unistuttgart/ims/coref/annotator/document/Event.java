package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.tcas.Annotation;

public interface Event {
	public enum Type {
		/**
		 * This event creates a new object under an existing one. Argument 1: newly
		 * created object, argument 2: the parent. If the second argument is null, we
		 * assume to create a new top level thing.
		 */
		Add, Remove, Update,
		/**
		 * This describes moving arg1 from arg2 to arg3. Arg3 becomes the new parent,
		 * arg2 is the old one.
		 */
		Move, Merge, Op
	};

	Type getType();

	Op getOp();

	public static <T extends Annotation> AnnotationEvent get(Type type, T annotation) {
		return new AnnotationEvent(type, annotation);
	}

	public static FeatureStructureEvent get(Type type, FeatureStructure... fs) {
		return new FeatureStructureEvent(type, fs);
	}

}
