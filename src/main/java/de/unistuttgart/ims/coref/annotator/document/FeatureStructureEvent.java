package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.cas.FeatureStructure;

public class FeatureStructureEvent<T extends FeatureStructure> implements Event {
	T argument1;
	Type eventType;

	public FeatureStructureEvent(Type eventType, T featureStructure) {
		this.eventType = eventType;
		this.argument1 = featureStructure;
	}

	@Override
	public Type getType() {
		return eventType;
	}

	public T getArgument1() {
		return argument1;
	}

	public void setArgument1(T featureStructure) {
		this.argument1 = featureStructure;
	}

	public void setType(Type eventType) {
		this.eventType = eventType;
	}

	@Override
	public Op getOp() {
		return null;
	}
}
