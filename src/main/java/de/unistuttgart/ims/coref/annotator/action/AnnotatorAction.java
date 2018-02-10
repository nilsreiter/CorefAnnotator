package de.unistuttgart.ims.coref.annotator.action;

import javax.swing.AbstractAction;

import de.unistuttgart.ims.coref.annotator.Annotator;

public abstract class AnnotatorAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	Annotator mainApplication;

	public AnnotatorAction(Annotator mApp) {
		this.mainApplication = mApp;
	}
}
