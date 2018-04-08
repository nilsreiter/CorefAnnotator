package de.unistuttgart.ims.coref.annotator.action;

import org.kordamp.ikonli.Ikon;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public abstract class DocumentModelAction extends AnnotatorAction {

	private static final long serialVersionUID = 1L;

	protected DocumentModel documentModel;

	public DocumentModelAction(DocumentModel dw, String stringKey, Ikon... ikon) {
		super(Annotator.app, stringKey, ikon);
		this.documentModel = dw;
	}

	public DocumentModelAction(DocumentModel dw, String stringKey, boolean isKey, Ikon... ikon) {
		super(Annotator.app, stringKey, isKey, ikon);
		this.documentModel = dw;
	}

	public DocumentModelAction(DocumentModel dw, Ikon ikon) {
		super(Annotator.app, ikon);
		this.documentModel = dw;
	}

}
