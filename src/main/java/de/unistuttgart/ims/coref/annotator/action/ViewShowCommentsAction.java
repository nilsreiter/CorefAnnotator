package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public class ViewShowCommentsAction extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	public ViewShowCommentsAction(DocumentWindow dw) {
		super(dw, Constants.Strings.ACTION_SHOW_COMMENTS, MaterialDesign.MDI_MESSAGE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().getCommentsWindow().setVisible(true);
	}

}