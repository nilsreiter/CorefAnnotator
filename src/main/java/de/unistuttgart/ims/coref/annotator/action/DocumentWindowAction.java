package de.unistuttgart.ims.coref.annotator.action;

import org.kordamp.ikonli.Ikon;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public abstract class DocumentWindowAction extends AnnotatorAction {

	private static final long serialVersionUID = 1L;

	public DocumentWindowAction(DocumentWindow dw, Ikon ikon) {
		super(dw.getMainApplication(), ikon);
	}

}
