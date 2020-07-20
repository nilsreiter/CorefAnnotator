package de.unistuttgart.ims.coref.annotator;

public enum TypeSystemVersion {
	LEGACY, v1, v2;

	public static TypeSystemVersion getCurrent() {
		return TypeSystemVersion.v2;
	}
}
