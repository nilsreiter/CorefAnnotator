package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import de.unistuttgart.ims.coref.annotator.api.Comment;
import de.unistuttgart.ims.coref.annotator.comp.PanelFactory;
import de.unistuttgart.ims.coref.annotator.comp.PanelList;

public class CommentWindow extends JDialog {

	private static final long serialVersionUID = 1L;

	DocumentWindow mainWindow;
	CoreferenceModel documentModel;

	CoreferenceModel.CommentsModel commentsListModel;

	// Components
	PanelList<Comment, CommentPanel> commentList;

	public CommentWindow(DocumentWindow mainWindow, CoreferenceModel documentModel) {
		this.mainWindow = mainWindow;
		this.documentModel = documentModel;
		this.commentsListModel = documentModel.getCommentsModel();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				initialiseWindow();
			}
		});
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				initialiseList();
			}
		});

	}

	protected void initialiseList() {
		this.commentsListModel.load();
	}

	protected void initialiseWindow() {
		commentList = new PanelList<Comment, CommentPanel>(new PanelFactory<Comment, CommentPanel>() {
			@Override
			public CommentPanel getPanel(Comment object) {
				return new CommentPanel(CommentWindow.this, commentsListModel, object);
			}
		});
		commentList.setModel(commentsListModel);

		getContentPane().add(new JScrollPane(commentList), BorderLayout.CENTER);

		setTitle("Comments");
		getContentPane().setPreferredSize(new Dimension(250, mainWindow.getHeight() - 20));
		pack();
		setLocation(new Point(mainWindow.getX() + mainWindow.getWidth(), mainWindow.getY()));
	}

	public void enterNewComment(int begin, int end) {
		Comment c = commentsListModel.add("", mainWindow.getMainApplication().getPreferences()
				.get(Constants.CFG_ANNOTATOR_ID, Defaults.CFG_ANNOTATOR_ID), begin, end);
		commentList.getPanel(c).fireEditAction();
	}

}
