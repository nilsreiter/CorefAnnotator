package de.unistuttgart.ims.coref.annotator.document;

public class DocumentState {
	int historySize;
	String language;

	public DocumentState(DocumentModel documentModel) {
		this.historySize = documentModel.getCoreferenceModel().getHistory().size();
		this.language = documentModel.getJcas().getDocumentLanguage();
	}

	public int getHistorySize() {
		return historySize;
	}

	public void setHistorySize(int historySize) {
		this.historySize = historySize;
	}

	public String getLanguage() {
		return language;
	}

}
