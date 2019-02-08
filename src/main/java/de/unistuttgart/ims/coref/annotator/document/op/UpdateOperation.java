package de.unistuttgart.ims.coref.annotator.document.op;

import org.apache.uima.cas.FeatureStructure;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

public abstract class UpdateOperation<T extends FeatureStructure> implements Operation {
	ImmutableList<T> objects;

	@SafeVarargs
	public UpdateOperation(T... objects) {
		this.objects = Lists.immutable.of(objects);
	}

	public UpdateOperation(Iterable<T> objects) {
		this.objects = Lists.immutable.withAll(objects);
	}

	public ImmutableList<T> getObjects() {
		return objects;
	}

	public void setObjects(ImmutableList<T> objects) {
		this.objects = objects;
	}

}