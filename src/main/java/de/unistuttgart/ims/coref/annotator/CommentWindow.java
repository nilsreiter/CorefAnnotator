package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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
		this.initialiseWindow();
		this.initialiseList();

	}

	protected void initialiseList() {
		this.commentsListModel.load();
	}

	protected void initialiseWindow() {
		commentList = new PanelList<Comment>(new PanelFactory<Comment>() {
			@Override
			public JPanel getPanel(Comment object) {
				return new CommentPanel(object);
			}

		});
		commentList.setModel(commentsListModel);

		editArea = new JTextArea();
		editArea.setRows(5);
		editArea.setColumns(30);
		editArea.setLineWrap(true);
		editArea.setWrapStyleWord(true);

		// JScrollPane scrollPane = new JScrollPane(commentList,
		// JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		// JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		getContentPane().add(new JScrollPane(commentList), BorderLayout.CENTER);

		setTitle("Comments");
		setLocation(mainWindow.getLocation().x + mainWindow.getWidth(), mainWindow.getLocation().y);

		setVisible(true);

		getContentPane().setPreferredSize(new Dimension(200, 800));
	}

	public void enterNewComment() {

	}

	class CellRenderer implements PanelFactory<Comment> {

		@Override
		public JPanel getPanel(Comment object) {
			return new CommentPanel(object);
		}

	}

}
