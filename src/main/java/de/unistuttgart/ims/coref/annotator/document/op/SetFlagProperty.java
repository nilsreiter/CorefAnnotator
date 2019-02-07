package de.unistuttgart.ims.coref.annotator.document.op;

import de.unistuttgart.ims.coref.annotator.api.v1.Flag;

public class SetFlagProperty implements FlagModelOperation {
	public enum FlagProperty {
		LABEL, ICON, TARGETCLASS, KEY;
	};

	Flag flag;
	FlagProperty flagProperty;
	Object newValue;
	Object oldValue;

	public SetFlagProperty(Flag flag, FlagProperty property, Object value) {
		this.flag = flag;
		this.flagProperty = property;
		this.newValue = value;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public void setOldValue(Object oldValue) {
		this.oldValue = oldValue;
	}

	public Flag getFlag() {
		return flag;
	}

	public FlagProperty getFlagProperty() {
		return flagProperty;
	}

	public Object getNewValue() {
		return newValue;
	}
}
