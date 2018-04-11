package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class ShowMentionInTreeAction extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	Mention m;

	public ShowMentionInTreeAction(DocumentWindow documentWindow, Mention m) {
		super(documentWindow, Strings.ACTION_SHOW_MENTION_IN_TREE, MaterialDesign.MDI_FILE_TREE);
		this.m = m;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object[] path = getTarget().getDocumentModel().getTreeModel().getPathToRoot(m);
		TreePath tp = new TreePath(path);
		this.getTarget().getTree().setSelectionPath(tp);
		this.getTarget().getTree().scrollPathToVisible(tp);

	}

}