package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public class RemoveSingletons extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	public RemoveSingletons(DocumentWindow documentWindow) {
		super(documentWindow, "action.remove_singletons", MaterialDesign.MDI_ACCOUNT_REMOVE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().getDocumentModel().edit(new de.unistuttgart.ims.coref.annotator.document.op.RemoveSingletons());
	}

}
