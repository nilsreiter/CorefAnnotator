package de.unistuttgart.ims.coref.annotator.document.op;

import org.apache.uima.cas.FeatureStructure;

public class AddFlag implements FlagModelOperation, RedoableOperation {

	Class<? extends FeatureStructure> targetClass;

	public AddFlag(Class<? extends FeatureStructure> targetClass) {
		this.targetClass = targetClass;
	}

	public Class<? extends FeatureStructure> getTargetClass() {
		return targetClass;
	}
}
