package de.unistuttgart.ims.coref.annotator;

public interface SearchContainer {

	int getContexts();

	String getText();

	DocumentWindow getDocumentWindow();

	void pack();
}
