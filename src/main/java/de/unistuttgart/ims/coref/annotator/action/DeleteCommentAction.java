package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.api.Comment;

public class DeleteCommentAction extends AnnotatorAction {

	private static final long serialVersionUID = 1L;
	Comment comment;
	CoreferenceModel.CommentsModel model;

	public DeleteCommentAction(CoreferenceModel.CommentsModel model, Comment comment) {
		super(null, Constants.Strings.ACTION_COMMENT_DELETE, MaterialDesign.MDI_DELETE);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Constants.Strings.ACTION_COMMENT_DELETE_TOOLTIP));
		this.comment = comment;
		this.model = model;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		model.remove(comment);
	}

}