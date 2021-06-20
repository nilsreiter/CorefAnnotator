package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.AbstractTextWindow;
import de.unistuttgart.ims.coref.annotator.CompareMentionsWindow;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.api.v2.MentionSurface;

public class SelectNextMentionAction extends TargetedIkonAction<AbstractTextWindow> {
	private static final long serialVersionUID = 1L;

	public SelectNextMentionAction(AbstractTextWindow dw) {
		super(dw, MaterialDesign.MDI_ARROW_RIGHT);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int high = getTarget().getTextPane().getSelectionStart() + 1;

		MentionSurface nextMention = null;
		if (getTarget() instanceof DocumentWindow) {
			nextMention = getTarget().getDocumentModel().getCoreferenceModel().getNextMentionSurface(high);
		} else if (getTarget() instanceof CompareMentionsWindow) {
			nextMention = ((CompareMentionsWindow) getTarget()).getNextMentionSurface(high);
		}
		if (nextMention != null)
			getTarget().annotationSelected(nextMention);
	}

}