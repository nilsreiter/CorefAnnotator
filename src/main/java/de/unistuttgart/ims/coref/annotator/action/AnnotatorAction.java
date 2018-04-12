package de.unistuttgart.ims.coref.annotator.action;

import javax.swing.Action;

import org.kordamp.ikonli.Ikon;

import de.unistuttgart.ims.coref.annotator.Annotator;

@Deprecated
public abstract class AnnotatorAction extends IkonAction {

	private static final long serialVersionUID = 1L;

	Annotator mainApplication = Annotator.app;

	public AnnotatorAction(Annotator mApp, Ikon... ikon) {
		super(ikon);
		this.mainApplication = mApp;

	}

	public AnnotatorAction(Annotator mApp, String stringKey, Ikon... ikon) {
		super(ikon);
		this.mainApplication = mApp;
		putValue(Action.NAME, Annotator.getString(stringKey));

	}

	public AnnotatorAction(Annotator mApp, String stringKey, boolean isKey, Ikon... ikon) {
		super(ikon);
		this.mainApplication = mApp;
		putValue(Action.NAME, (isKey ? Annotator.getString(stringKey) : stringKey));

	}

}
