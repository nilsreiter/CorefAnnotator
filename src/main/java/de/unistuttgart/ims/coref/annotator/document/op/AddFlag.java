package de.unistuttgart.ims.coref.annotator.document.op;

import org.apache.uima.cas.FeatureStructure;

import de.unistuttgart.ims.coref.annotator.api.v1.Flag;

public class AddFlag implements FlagModelOperation, RedoableOperation {

	Class<? extends FeatureStructure> targetClass;
	Flag addedFlag;

	public AddFlag(Class<? extends FeatureStructure> targetClass) {
		this.targetClass = targetClass;
	}

	public Class<? extends FeatureStructure> getTargetClass() {
		return targetClass;
	}

	public Flag getAddedFlag() {
		return addedFlag;
	}

	public void setAddedFlag(Flag addedFlag) {
		this.addedFlag = addedFlag;
	}
}
