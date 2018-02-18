package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.CoreferenceModel.CommentsModel;
import de.unistuttgart.ims.coref.annotator.action.DeleteCommentAction;
import de.unistuttgart.ims.coref.annotator.action.IkonAction;
import de.unistuttgart.ims.coref.annotator.api.Comment;

public class CommentPanel extends JPanel {

	public class SaveCommentAction extends IkonAction {
		CommentsModel model;
		private static final long serialVersionUID = 1L;

		public SaveCommentAction(CommentsModel model) {
			super(MaterialDesign.MDI_CHECKBOX_MARKED);
			putValue(Action.NAME, "save");
			this.model = model;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			comment.setValue(textArea.getText());
			model.update(comment);
			saveAction.setEnabled(false);
			textArea.setEditable(false);
		}

	}

	public class EditCommentAction extends IkonAction {

		private static final long serialVersionUID = 1L;

		public EditCommentAction() {
			super(MaterialDesign.MDI_MESSAGE_DRAW);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			textArea.setEditable(true);
			saveAction.setEnabled(true);
		}
	}

	private static final long serialVersionUID = 1L;

	Comment comment;
	JTextArea textArea;
	Action deleteAction, editAction, saveAction;

	public CommentPanel(CommentsModel model, Comment c) {

		deleteAction = new DeleteCommentAction(model, c);
		editAction = new EditCommentAction();
		saveAction = new SaveCommentAction(model);
		saveAction.setEnabled(false);

		comment = c;
		SpringLayout springs = new SpringLayout();
		setOpaque(true);
		setLayout(springs);
		// setPreferredSize(new Dimension(100, 50));

		textArea = new JTextArea();
		textArea.setText(c.getValue());
		textArea.setBackground(new Color(255, 255, 200));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setOpaque(true);
		textArea.setRows(3);
		textArea.setEditable(false);

		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.add(deleteAction);
		toolbar.add(editAction);
		toolbar.add(saveAction);

		add(textArea);
		add(toolbar);
		// add(editButton);
		// add(deleteButton);
		// add(saveButton);

		springs.putConstraint(SpringLayout.WEST, textArea, 10, SpringLayout.WEST, this);
		springs.putConstraint(SpringLayout.EAST, textArea, -10, SpringLayout.EAST, this);
		springs.putConstraint(SpringLayout.NORTH, textArea, 10, SpringLayout.NORTH, this);

		springs.putConstraint(SpringLayout.NORTH, toolbar, 10, SpringLayout.SOUTH, textArea);

		// springs.putConstraint(SpringLayout.SOUTH, toolbar, -10,
		// SpringLayout.SOUTH, this);
		// springs.putConstraint(SpringLayout.EAST, toolbar, -10,
		// SpringLayout.EAST, this);

	}

}