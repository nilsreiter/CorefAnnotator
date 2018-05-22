package de.unistuttgart.ims.coref.annotator;

public enum TypeSystemVersion {
	LEGACY, v1;

	public static TypeSystemVersion getCurrent() {
		return TypeSystemVersion.v1;
	}
}
