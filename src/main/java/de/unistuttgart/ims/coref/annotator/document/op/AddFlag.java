package de.unistuttgart.ims.coref.annotator.document.op;

import org.apache.uima.cas.FeatureStructure;
import org.kordamp.ikonli.Ikon;

import de.unistuttgart.ims.coref.annotator.api.v1.Flag;

public class AddFlag implements FlagModelOperation, RedoableOperation {

	Class<? extends FeatureStructure> targetClass;
	String label = null;
	Ikon icon = null;
	String key = null;
	Flag addedFlag;

	public AddFlag(Class<? extends FeatureStructure> targetClass) {
		this.targetClass = targetClass;
	}

	public AddFlag(String key, String label, Ikon icon, Class<? extends FeatureStructure> targetClass) {
		this.key = key;
		this.label = label;
		this.icon = icon;
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Ikon getIcon() {
		return icon;
	}

	public void setIcon(Ikon icon) {
		this.icon = icon;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
