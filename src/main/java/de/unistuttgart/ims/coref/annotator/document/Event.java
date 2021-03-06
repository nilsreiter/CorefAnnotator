package de.unistuttgart.ims.coref.annotator.document;

import java.util.List;

import org.apache.uima.cas.FeatureStructure;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.document.op.Operation;

public interface Event extends Iterable<FeatureStructure> {
	public enum Type {
		/**
		 * This event creates a new object under an existing one. Argument 1: the
		 * parent, argument 2: the new object. If the first argument is null, we assume
		 * to create a new top level thing.
		 */
		Add,
		/**
		 * Remove encodes an event in which arg2-argn are removed from arg1.
		 */
		Remove,
		/**
		 * An update event is an internal event on arg1-argn that does not change the
		 * tree structure. If a color is changed, update events are fired for all
		 * mentions of the entity (this is done in CoreferenceModel).
		 */
		Update,
		/**
		 * This describes moving arg3 to argn from arg1 to arg2. Arg2 becomes the new
		 * parent, arg1 is the old one.
		 */
		Move, Merge, Op,
		/**
		 * Once a listener is registered with a model, the init-event is sent.
		 */
		Init
	};

	Type getType();

	Operation getOp();

	int getArity();

	public static FeatureStructureEvent get(Model src, Type type, FeatureStructure fs,
			Iterable<? extends FeatureStructure> fsi) {
		MutableList<FeatureStructure> l = Lists.mutable.withAll(fsi);
		l.add(0, fs);
		return new FeatureStructureEvent(src, type, l);
	}

	public static FeatureStructureEvent get(Model src, Type type, FeatureStructure arg1, FeatureStructure arg2,
			ImmutableList<? extends FeatureStructure> fsi) {
		MutableList<FeatureStructure> l = Lists.mutable.withAll(fsi);
		l.add(0, arg2);
		l.add(0, arg1);
		return new FeatureStructureEvent(src, type, l);
	}

	public static FeatureStructureEvent get(Model src, Type type, Iterable<? extends FeatureStructure> fs) {
		return new FeatureStructureEvent(src, type, fs);
	}

	public static FeatureStructureEvent get(Model src, Type type, List<FeatureStructure> fs) {
		return new FeatureStructureEvent(src, type, fs);
	}

	public static FeatureStructureEvent get(Model src, Type type, FeatureStructure... fs) {
		return new FeatureStructureEvent(src, type, fs);
	}

}
