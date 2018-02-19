package de.unistuttgart.ims.coref.annotator.action;

import org.kordamp.ikonli.Ikon;

import de.unistuttgart.ims.coref.annotator.Annotator;

public abstract class AnnotatorAction extends IkonAction {

	private static final long serialVersionUID = 1L;

	Annotator mainApplication;

	public AnnotatorAction(Annotator mApp, Ikon ikon) {
		super(ikon);
		this.mainApplication = mApp;

	}

	public AnnotatorAction(Annotator mApp, Ikon ikon, String stringKey) {
		super(ikon, stringKey);
		this.mainApplication = mApp;

	}
}
