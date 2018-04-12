package de.unistuttgart.ims.coref.annotator.action;

import org.kordamp.ikonli.Ikon;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public abstract class DocumentWindowAction extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	public DocumentWindowAction(DocumentWindow dw, String stringKey, Ikon... ikon) {
		super(dw, stringKey, ikon);
	}

	public DocumentWindowAction(DocumentWindow dw, String stringKey, boolean isKey, Ikon... ikon) {
		super(dw, stringKey, isKey, ikon);
	}

	public DocumentWindowAction(DocumentWindow dw, Ikon ikon) {
		super(dw, ikon);
	}

	public DocumentWindow getDocumentWindow() {
		return target;
	}

}
