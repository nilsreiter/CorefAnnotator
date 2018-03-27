package de.unistuttgart.ims.coref.annotator.document;

public class DocumentState {
	int historySize;

	public DocumentState(DocumentModel documentModel) {
		this.historySize = documentModel.getCoreferenceModel().getHistory().size();

	}

	public int getHistorySize() {
		return historySize;
	}

	public void setHistorySize(int historySize) {
		this.historySize = historySize;
	}
}
