package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.RelationEditor;

public class ShowRelationEditor extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	public ShowRelationEditor(DocumentWindow dw) {
		super(dw, "action.edit_relations", MaterialDesign.MDI_FLAG);
	}

	@SuppressWarnings("unused")
	@Override
	public void actionPerformed(ActionEvent e) {
		new RelationEditor(getTarget().getDocumentModel(), getTarget());
	}

}
