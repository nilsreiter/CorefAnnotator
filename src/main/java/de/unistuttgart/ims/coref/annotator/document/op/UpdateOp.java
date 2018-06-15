package de.unistuttgart.ims.coref.annotator.document.op;

import org.apache.uima.cas.FeatureStructure;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.document.Op;

public abstract class UpdateOp<T extends FeatureStructure> implements Op {
	ImmutableList<T> objects;

	@SafeVarargs
	public UpdateOp(T... objects) {
		this.objects = Lists.immutable.of(objects);
	}

	public UpdateOp(Iterable<T> objects) {
		this.objects = Lists.immutable.withAll(objects);
	}

	public ImmutableList<T> getObjects() {
		return objects;
	}

	public void setObjects(ImmutableList<T> objects) {
		this.objects = objects;
	}

}