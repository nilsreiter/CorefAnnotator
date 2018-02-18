package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.api.Comment;

public class DeleteCommentAction extends IkonAction {

	private static final long serialVersionUID = 1L;
	Comment comment;

	public DeleteCommentAction(Comment comment) {
		super(MaterialDesign.MDI_MESSAGE_BULLETED_OFF);
		putValue(Action.NAME, "Delete comment");
		this.comment = comment;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

}