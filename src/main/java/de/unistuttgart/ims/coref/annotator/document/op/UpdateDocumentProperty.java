package de.unistuttgart.ims.coref.annotator.document.op;

import org.apache.uima.jcas.cas.Sofa;

public class UpdateDocumentProperty extends UpdateOperation<Sofa> implements DocumentModelOperation {
	public enum DocumentProperty {
		LANGUAGE
	}

	DocumentProperty documentProperty;
	Object newValue;
	Object oldValue;

	public UpdateDocumentProperty(DocumentProperty documentProperty, Object newValue) {
		this.documentProperty = documentProperty;
		this.newValue = newValue;
	}

	public DocumentProperty getDocumentProperty() {
		return documentProperty;
	}

	public void setDocumentProperty(DocumentProperty documentProperty) {
		this.documentProperty = documentProperty;
	}

	public Object getNewValue() {
		return newValue;
	}

	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public void setOldValue(Object oldValue) {
		this.oldValue = oldValue;
	}

}
