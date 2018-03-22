package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.cas.FeatureStructure;

public class FeatureStructureEvent implements Event {
	FeatureStructure[] arguments;
	Type eventType;

	public FeatureStructureEvent(Type eventType, FeatureStructure... args) {
		this.eventType = eventType;
		this.arguments = args;
	}

	@Override
	public Type getType() {
		return eventType;
	}

	public FeatureStructure getArgument1() {
		return arguments[0];
	}

	public void setArgument1(FeatureStructure featureStructure) {
		this.arguments[0] = featureStructure;
	}

	public void setType(Type eventType) {
		this.eventType = eventType;
	}

	@Override
	public Op getOp() {
		return null;
	}

	public FeatureStructure getArgument2() {
		return arguments[1];
	}

	public void setArgument2(FeatureStructure argument2) {
		this.arguments[1] = argument2;
	}

	public FeatureStructure getArgument(int i) {
		return this.arguments[i];
	}
}
