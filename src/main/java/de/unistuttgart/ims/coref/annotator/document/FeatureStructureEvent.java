package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.cas.FeatureStructure;

public class FeatureStructureEvent implements Event {
	FeatureStructure featureStructure;
	Type eventType;

	public FeatureStructureEvent(Type eventType, FeatureStructure featureStructure) {
		this.eventType = eventType;
		this.featureStructure = featureStructure;
	}

	@Override
	public Type getType() {
		return eventType;
	}

	public FeatureStructure getFeatureStructure() {
		return featureStructure;
	}

	public void setFeatureStructure(FeatureStructure featureStructure) {
		this.featureStructure = featureStructure;
	}

	public void setType(Type eventType) {
		this.eventType = eventType;
	}
}
