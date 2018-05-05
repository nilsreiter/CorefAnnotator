package de.unistuttgart.ims.coref.annotator;

public enum TypeSystemVersion {
	LEGACY, v1_0;

	static TypeSystemVersion getCurrent() {
		return TypeSystemVersion.v1_0;
	}
}
