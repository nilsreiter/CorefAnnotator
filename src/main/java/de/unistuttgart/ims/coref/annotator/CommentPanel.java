package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.CoreferenceModel.CommentsModel;
import de.unistuttgart.ims.coref.annotator.action.DeleteCommentAction;
import de.unistuttgart.ims.coref.annotator.action.IkonAction;
import de.unistuttgart.ims.coref.annotator.api.Comment;
import de.unistuttgart.ims.coref.annotator.comp.PanelList;

public class CommentPanel extends JPanel {

	private static final Color TEXT_BACKGROUND_ENABLED = new Color(255, 255, 200);
	private static final Color TEXT_BACKGROUND_DISABLED = Color.WHITE;

	private static final Color TEXT_FOREGROUND_ENABLED = Color.BLACK;
	private static final Color TEXT_FOREGROUND_DISABLED = Color.GRAY;

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

			@SuppressWarnings("unchecked")
			PanelList<Comment> parent = (PanelList<Comment>) CommentPanel.this.getParent();
			parent.setSelection(null);

			comment.setValue(textArea.getText());
			model.update(comment);
			saveAction.setEnabled(false);
			textArea.setEditable(false);
			editAction.setEnabled(true);
			deleteAction.setEnabled(true);
		}

	}

	public class EditCommentAction extends IkonAction {

		private static final long serialVersionUID = 1L;

		public EditCommentAction() {
			super(MaterialDesign.MDI_MESSAGE_DRAW);
			putValue(Action.NAME, "edit");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			textArea.setEditable(true);
			saveAction.setEnabled(true);
			deleteAction.setEnabled(false);
			editAction.setEnabled(false);

			@SuppressWarnings("unchecked")
			PanelList<Comment> parent = (PanelList<Comment>) CommentPanel.this.getParent();
			parent.setSelection(comment);

			textArea.grabFocus();

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
		// setOpaque(true);
		setLayout(springs);

		textArea = new JTextArea();
		textArea.setText(c.getValue());
		textArea.setForeground(TEXT_FOREGROUND_ENABLED);
		textArea.setBackground(TEXT_BACKGROUND_ENABLED);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setOpaque(true);
		textArea.setRows(3);
		textArea.setEditable(false);
		textArea.setColumns(19);

		JToolBar toolbar = new JToolBar();
		toolbar.setOrientation(JToolBar.HORIZONTAL);
		toolbar.setFloatable(false);
		toolbar.add(deleteAction).setHideActionText(true);
		toolbar.add(editAction).setHideActionText(true);
		toolbar.add(saveAction).setHideActionText(true);

		add(new JScrollPane(textArea));
		add(toolbar);

		springs.putConstraint(SpringLayout.WEST, textArea, 10, SpringLayout.WEST, this);
		springs.putConstraint(SpringLayout.EAST, textArea, -10, SpringLayout.EAST, this);
		springs.putConstraint(SpringLayout.NORTH, textArea, 10, SpringLayout.NORTH, this);

		springs.putConstraint(SpringLayout.SOUTH, toolbar, -10, SpringLayout.SOUTH, this);
		springs.putConstraint(SpringLayout.SOUTH, textArea, 20, SpringLayout.NORTH, toolbar);
		springs.putConstraint(SpringLayout.WEST, toolbar, 10, SpringLayout.WEST, this);

		setMinimumSize(new Dimension(250, 120));
		setMaximumSize(new Dimension(250, 120));
		setBorder(new TitledBorder(comment.getAuthor()));
	}

	public void fireEditAction() {
		editAction.actionPerformed(null);
	}

	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		editAction.setEnabled(b);
		deleteAction.setEnabled(b);

		if (b) {
			setForeground(Color.BLACK);
			setOpaque(true);
			textArea.setBackground(TEXT_BACKGROUND_ENABLED);
			textArea.setForeground(TEXT_FOREGROUND_ENABLED);
		} else {
			setOpaque(false);
			setForeground(new Color(200, 200, 200));
			textArea.setBackground(TEXT_BACKGROUND_DISABLED);
			textArea.setForeground(TEXT_FOREGROUND_DISABLED);
		}
	}

}