package de.unistuttgart.ims.coref.annotator.action;

import org.kordamp.ikonli.Ikon;

import de.unistuttgart.ims.coref.annotator.AnnotationView;

public abstract class DocumentWindowAction extends AnnotatorAction {

	private static final long serialVersionUID = 1L;

	protected AnnotationView annotationView;

	public DocumentWindowAction(AnnotationView dw, String stringKey, Ikon... ikon) {
		super(dw.getMainApplication(), stringKey, ikon);
		this.annotationView = dw;
	}

	public DocumentWindowAction(AnnotationView dw, String stringKey, boolean isKey, Ikon... ikon) {
		super(dw.getMainApplication(), stringKey, isKey, ikon);
		this.annotationView = dw;
	}

	public DocumentWindowAction(AnnotationView dw, Ikon ikon) {
		super(dw.getMainApplication(), ikon);
		this.annotationView = dw;
	}

}
