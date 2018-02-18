package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.api.Comment;

public class DeleteCommentAction extends IkonAction {

	private static final long serialVersionUID = 1L;
	Comment comment;
	CoreferenceModel.CommentsModel model;

	public DeleteCommentAction(CoreferenceModel.CommentsModel model, Comment comment) {
		super(MaterialDesign.MDI_MESSAGE_BULLETED_OFF);
		putValue(Action.NAME, "Delete comment");
		this.comment = comment;
		this.model = model;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		model.remove(comment);
	}

}