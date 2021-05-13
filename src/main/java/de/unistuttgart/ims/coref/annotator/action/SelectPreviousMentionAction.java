package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.AbstractTextWindow;
import de.unistuttgart.ims.coref.annotator.CompareMentionsWindow;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.api.v2.MentionSurface;

public class SelectPreviousMentionAction extends TargetedIkonAction<AbstractTextWindow> {

	public SelectPreviousMentionAction(AbstractTextWindow dw) {
		super(dw, MaterialDesign.MDI_ARROW_LEFT);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		int low = getTarget().getTextPane().getSelectionEnd() - 1;
		MentionSurface nextMention = null;
		if (getTarget() instanceof DocumentWindow) {
			nextMention = getTarget().getDocumentModel().getCoreferenceModel().getPreviousMentionSurface(low);
		} else if (getTarget() instanceof CompareMentionsWindow) {
			nextMention = ((CompareMentionsWindow) getTarget()).getPreviousMentionSurface(low);
		}

		if (nextMention != null)
			getTarget().annotationSelected(nextMention);

	}

}