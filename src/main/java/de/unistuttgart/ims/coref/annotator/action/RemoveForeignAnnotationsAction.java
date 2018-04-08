package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public class RemoveForeignAnnotationsAction extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	public RemoveForeignAnnotationsAction(DocumentWindow dw) {
		super(dw, MaterialDesign.MDI_PHARMACY);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		documentWindow.getDocumentModel().removeForeignAnnotations();
	}

}
