package de.unistuttgart.ims.coref.annotator.inspector;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.uima.jcas.cas.TOP;

import de.unistuttgart.ims.coref.annotator.comp.CABean;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public abstract class Issue implements CABean {
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	String description;
	boolean solvable = true;
	DocumentModel documentModel;

	public Issue(DocumentModel documentModel) {
		this.documentModel = documentModel;
	}

	public abstract IssueType getIssueType();

	public boolean isSolvable() {
		return solvable;
	}

	public void fireSolve() {
		if (solve()) {
			pcs.firePropertyChange("solvable", true, false);
			solvable = false;
		}
	};

	public abstract boolean solve();

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	public static abstract class InstanceIssue<T extends TOP> extends Issue {

		T instance;

		public InstanceIssue(DocumentModel documentModel) {
			super(documentModel);
		}

		public T getInstance() {
			return instance;
		}

		public void setInstance(T instance) {
			this.instance = instance;
		}
	}

	public DocumentModel getDocumentModel() {
		return documentModel;
	}

	public void setDocumentModel(DocumentModel documentModel) {
		this.documentModel = documentModel;
	}

}
