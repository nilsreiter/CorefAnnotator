package de.unistuttgart.ims.coref.annotator.action;

import org.kordamp.ikonli.Ikon;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public abstract class DocumentWindowAction extends AnnotatorAction {

	private static final long serialVersionUID = 1L;

	protected DocumentWindow documentWindow;

	public DocumentWindowAction(DocumentWindow dw, String stringKey, Ikon... ikon) {
		super(dw.getMainApplication(), stringKey, ikon);
		this.documentWindow = dw;
	}

	public DocumentWindowAction(DocumentWindow dw, String stringKey, boolean isKey, Ikon... ikon) {
		super(dw.getMainApplication(), stringKey, isKey, ikon);
		this.documentWindow = dw;
	}

	public DocumentWindowAction(DocumentWindow dw, Ikon ikon) {
		super(dw.getMainApplication(), ikon);
		this.documentWindow = dw;
	}

}
