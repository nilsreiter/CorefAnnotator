package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import de.unistuttgart.ims.coref.annotator.CoreferenceModel.CommentsModel;
import de.unistuttgart.ims.coref.annotator.action.DeleteCommentAction;
import de.unistuttgart.ims.coref.annotator.action.ExitAction;
import de.unistuttgart.ims.coref.annotator.api.Comment;

class CommentPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	Comment comment;
	JTextArea textArea;
	Action deleteAction, editAction;

	public CommentPanel(CommentsModel model, Comment c) {
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
		JButton deleteButton = new JButton(new DeleteCommentAction(model, c));
		JButton editButton = new JButton(new ExitAction());

		add(textArea);
		add(editButton);
		add(deleteButton);

		springs.putConstraint(SpringLayout.WEST, textArea, 10, SpringLayout.WEST, this);
		springs.putConstraint(SpringLayout.EAST, textArea, -10, SpringLayout.EAST, this);
		springs.putConstraint(SpringLayout.NORTH, textArea, 10, SpringLayout.NORTH, this);

		springs.putConstraint(SpringLayout.NORTH, editButton, 10, SpringLayout.SOUTH, textArea);
		springs.putConstraint(SpringLayout.NORTH, deleteButton, 10, SpringLayout.SOUTH, textArea);

		springs.putConstraint(SpringLayout.SOUTH, deleteButton, -10, SpringLayout.SOUTH, this);
		springs.putConstraint(SpringLayout.SOUTH, editButton, -10, SpringLayout.SOUTH, this);
		springs.putConstraint(SpringLayout.EAST, editButton, 10, SpringLayout.WEST, deleteButton);
		springs.putConstraint(SpringLayout.EAST, deleteButton, -10, SpringLayout.EAST, this);

	}

}