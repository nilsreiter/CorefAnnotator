package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.tree.TreePath;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class ShowMentionInTreeAction extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	Mention m;

	public ShowMentionInTreeAction(DocumentWindow documentWindow, Mention m) {
		super(documentWindow, MaterialDesign.MDI_FILE_TREE);
		putValue(Action.NAME, Annotator.getString("action.show_mention_in_tree"));
		this.m = m;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		CATreeNode node = documentWindow.getCoreferenceModel().getNode(m);
		Object[] path = documentWindow.getCoreferenceModel().getPathToRoot(node);
		TreePath tp = new TreePath(path);
		this.documentWindow.getTree().setSelectionPath(tp);
		this.documentWindow.getTree().scrollPathToVisible(tp);

	}

}