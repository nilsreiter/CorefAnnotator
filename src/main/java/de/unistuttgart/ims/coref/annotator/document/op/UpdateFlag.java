package de.unistuttgart.ims.coref.annotator.document.op;

import de.unistuttgart.ims.coref.annotator.api.v1.Flag;

public class UpdateFlag extends UpdateOp<Flag> implements FlagModelOperation {
	public enum FlagProperty {
		LABEL, ICON, TARGETCLASS, KEY;
	};

	FlagProperty flagProperty;
	Object newValue;
	Object oldValue;

	public UpdateFlag(Flag flag, FlagProperty property, Object value) {
		super(flag);
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
		return objects.getFirst();
	}

	public FlagProperty getFlagProperty() {
		return flagProperty;
	}

	public Object getNewValue() {
		return newValue;
	}
}
