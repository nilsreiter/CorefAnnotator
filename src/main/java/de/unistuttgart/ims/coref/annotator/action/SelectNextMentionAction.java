package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import  de.unistuttgart.ims.coref.annotator.api.v2.Mention;

public class SelectNextMentionAction extends TargetedIkonAction<DocumentWindow> {
	private static final long serialVersionUID = 1L;

	public SelectNextMentionAction(DocumentWindow dw) {
		super(dw, MaterialDesign.MDI_ARROW_RIGHT);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int high = getTarget().getTextPane().getSelectionEnd();

		Mention nextMention = getTarget().getDocumentModel().getCoreferenceModel().getNextMention(high);

		if (nextMention != null)
			getTarget().annotationSelected(nextMention);

	}

}