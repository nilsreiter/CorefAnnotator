package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.FlagEditor;

public class ShowFlagEditor extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	public ShowFlagEditor(DocumentWindow dw) {
		super(dw, "action.edit_flags", MaterialDesign.MDI_FLAG);
	}

	@SuppressWarnings("unused")
	@Override
	public void actionPerformed(ActionEvent e) {
		new FlagEditor(getTarget(), getTarget().getDocumentModel());
	}

}
