package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.AnnotationView;

public class ViewShowCommentsAction extends TargetedIkonAction<AnnotationView> {

	private static final long serialVersionUID = 1L;

	public ViewShowCommentsAction(AnnotationView dw) {
		super(dw, Constants.Strings.ACTION_SHOW_COMMENTS, MaterialDesign.MDI_MESSAGE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().getCommentsWindow().setVisible(true);
	}

}