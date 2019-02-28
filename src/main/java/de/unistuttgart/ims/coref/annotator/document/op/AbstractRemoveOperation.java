package de.unistuttgart.ims.coref.annotator.document.op;

import org.apache.uima.cas.FeatureStructure;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

public abstract class AbstractRemoveOperation<T extends FeatureStructure> {
	ImmutableList<T> featureStructures;

	public AbstractRemoveOperation(@SuppressWarnings("unchecked") T... featureStructures) {
		this.featureStructures = Lists.immutable.of(featureStructures);
	}

	public AbstractRemoveOperation(Iterable<T> featureStructures) {
		this.featureStructures = Lists.immutable.withAll(featureStructures);
	}

	public ImmutableList<T> getFeatureStructures() {
		return featureStructures;
	}

	public void setFeatureStructures(ImmutableList<T> featureStructures) {
		this.featureStructures = featureStructures;
	}

}
