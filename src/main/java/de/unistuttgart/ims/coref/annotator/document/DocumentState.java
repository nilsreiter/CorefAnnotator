package de.unistuttgart.ims.coref.annotator.document;

public class DocumentState {
	public DocumentState(int historySize) {
		this.historySize = historySize;
	}

	int historySize;

	public int getHistorySize() {
		return historySize;
	}

	public void setHistorySize(int historySize) {
		this.historySize = historySize;
	}
}
