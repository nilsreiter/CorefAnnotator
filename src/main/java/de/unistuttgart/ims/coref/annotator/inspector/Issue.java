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
	boolean solved = false;
	DocumentModel documentModel;

	public Issue(DocumentModel documentModel) {
		this.documentModel = documentModel;
	}

	public abstract IssueType getIssueType();

	public boolean isSolvable() {
		return solvable;
	}

	public void fireSolve(int solution) {
		if (solve(solution)) {
			pcs.firePropertyChange("solvable", true, false);
			solvable = false;
			pcs.firePropertyChange("solved", false, true);
		}
	};

	public abstract String getSolutionDescription(int solution);

	public abstract int getNumberOfSolutions();

	public abstract boolean solve(int solution);

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

	public DocumentModel getDocumentModel() {
		return documentModel;
	}

	public void setDocumentModel(DocumentModel documentModel) {
		this.documentModel = documentModel;
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

	public boolean isSolved() {
		return solved;
	}

	public void setSolved(boolean solved) {
		this.solved = solved;
	}
}
