package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import  de.unistuttgart.ims.coref.annotator.api.v2.Mention;

public class SelectPreviousMentionAction extends TargetedIkonAction<DocumentWindow> {

	public SelectPreviousMentionAction(DocumentWindow dw) {
		super(dw, MaterialDesign.MDI_ARROW_LEFT);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		int low = getTarget().getTextPane().getSelectionStart();
		Mention nextMention = getTarget().getDocumentModel().getCoreferenceModel().getPreviousMention(low);

		if (nextMention != null)
			getTarget().annotationSelected(nextMention);

	}

}