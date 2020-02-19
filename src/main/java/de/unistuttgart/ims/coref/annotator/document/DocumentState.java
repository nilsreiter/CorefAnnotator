package de.unistuttgart.ims.coref.annotator.document;

public class DocumentState {
	int historySize;
	String language;
	@Deprecated
	boolean unsavedChanges;
	boolean savable;

	@SuppressWarnings("deprecation")
	public DocumentState(DocumentModel documentModel) {
		this.historySize = documentModel.getHistory().size();
		this.language = documentModel.getJcas().getDocumentLanguage();
		this.unsavedChanges = documentModel.hasUnsavedChanges();
		this.savable = documentModel.isSavable();
	}

	public int getHistorySize() {
		return historySize;
	}

	public String getLanguage() {
		return language;
	}

	public boolean hasUnsavedChanges() {
		return savable;
	}

	public boolean isSavable() {
		return savable; // hasUnsavedChanges();
	}

}
