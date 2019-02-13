package de.unistuttgart.ims.coref.annotator.document;

public abstract class SubModel implements Model {

	protected boolean initialized = false;
	protected DocumentModel documentModel;

	public SubModel(DocumentModel documentModel) {
		this.documentModel = documentModel;
	}

	protected DocumentModel getDocumentModel() {
		return documentModel;
	}

	protected void initialize() {
		if (initialized)
			return;
		else {
			initializeOnce();
			initialized = true;
		}

	}

	protected void initializeOnce() {

	};

	public boolean isInitialized() {
		return initialized;
	}
}
