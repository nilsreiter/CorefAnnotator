package de.unistuttgart.ims.coref.annotator.action;

import org.kordamp.ikonli.Ikon;

import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

@Deprecated
public abstract class DocumentModelAction extends TargetedIkonAction<DocumentModel> {

	private static final long serialVersionUID = 1L;

	public DocumentModelAction(DocumentModel dw, String stringKey, Ikon... ikon) {
		super(dw, stringKey, ikon);
	}

	public DocumentModelAction(DocumentModel dw, String stringKey, boolean isKey, Ikon... ikon) {
		super(dw, stringKey, isKey, ikon);
	}

	public DocumentModelAction(DocumentModel dw, Ikon ikon) {
		super(dw, ikon);
	}

}
