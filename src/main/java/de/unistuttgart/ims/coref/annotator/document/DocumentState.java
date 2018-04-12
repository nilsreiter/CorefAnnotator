package de.unistuttgart.ims.coref.annotator.document;

public class DocumentState {
	int historySize;
	String language;
	boolean unsavedChanges;

	public DocumentState(DocumentModel documentModel) {
		this.historySize = documentModel.getCoreferenceModel().getHistory().size();
		this.language = documentModel.getJcas().getDocumentLanguage();
		this.unsavedChanges = documentModel.hasUnsavedChanges();
	}

	public int getHistorySize() {
		return historySize;
	}

	public String getLanguage() {
		return language;
	}

	public boolean hasUnsavedChanges() {
		return unsavedChanges;
	}

	public boolean isSavable() {
		return hasUnsavedChanges() || getHistorySize() > 0;
	}

}
