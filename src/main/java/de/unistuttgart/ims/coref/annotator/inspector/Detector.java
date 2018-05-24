package de.unistuttgart.ims.coref.annotator.inspector;

import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public interface Detector<T> {
	public static char[] whitespace = new char[] { ' ', '\n', '\t', '\r', '\f' };

	boolean detect(T object, char[] text);

	Issue getIssue(DocumentModel dm, T object);
}
