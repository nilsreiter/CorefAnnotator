package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public class UndoAction extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	public UndoAction(DocumentWindow dw) {
		super(dw, Constants.Strings.ACTION_UNDO, MaterialDesign.MDI_UNDO);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		documentWindow.getDocumentModel().getCoreferenceModel().undo();
	}

}
