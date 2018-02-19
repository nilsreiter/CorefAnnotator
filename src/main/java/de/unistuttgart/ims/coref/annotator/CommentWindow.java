package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import de.unistuttgart.ims.coref.annotator.api.Comment;
import de.unistuttgart.ims.coref.annotator.comp.PanelFactory;
import de.unistuttgart.ims.coref.annotator.comp.PanelList;

public class CommentWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	DocumentWindow mainWindow;
	CoreferenceModel documentModel;

	CoreferenceModel.CommentsModel commentsListModel;

	// Components
	JTextArea editArea;
	PanelList<Comment> commentList;

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
		commentList = new PanelList<Comment>(new PanelFactory<Comment>() {
			@Override
			public JPanel getPanel(Comment object) {
				return new CommentPanel(commentsListModel, object);
			}
		});
		commentList.setModel(commentsListModel);

		editArea = new JTextArea();
		editArea.setRows(5);
		editArea.setColumns(30);
		editArea.setLineWrap(true);
		editArea.setWrapStyleWord(true);

		getContentPane().add(new JScrollPane(commentList), BorderLayout.CENTER);

		setTitle("Comments");
		setLocation(mainWindow.getLocation().x + mainWindow.getWidth(), mainWindow.getLocation().y);

		setVisible(true);

		getContentPane().setPreferredSize(new Dimension(200, 800));
		setSize(new Dimension(200, 100));
	}

	public void enterNewComment(int begin, int end) {
		Comment c = commentsListModel.add("", mainWindow.getMainApplication().getPreferences()
				.get(Constants.CFG_ANNOTATOR_ID, Defaults.CFG_ANNOTATOR_ID), begin, end);
		((CommentPanel) commentList.getPanel(c)).fireEditAction();
	}

}
