package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.cas.FeatureStructure;

public class FeatureStructureEvent<T extends FeatureStructure> implements Event {
	T featureStructure;
	Type eventType;

	public FeatureStructureEvent(Type eventType, T featureStructure) {
		this.eventType = eventType;
		this.featureStructure = featureStructure;
	}

	@Override
	public Type getType() {
		return eventType;
	}

	public T getFeatureStructure() {
		return featureStructure;
	}

	public void setFeatureStructure(T featureStructure) {
		this.featureStructure = featureStructure;
	}

	public void setType(Type eventType) {
		this.eventType = eventType;
	}
}
