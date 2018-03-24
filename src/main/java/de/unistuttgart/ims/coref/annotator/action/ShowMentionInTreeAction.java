package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.AnnotationView;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class ShowMentionInTreeAction extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	Mention m;

	public ShowMentionInTreeAction(AnnotationView annotationView, Mention m) {
		super(annotationView, Strings.ACTION_SHOW_MENTION_IN_TREE, MaterialDesign.MDI_FILE_TREE);
		this.m = m;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object[] path = annotationView.getDocumentModel().getTreeModel().getPathToRoot(m);
		TreePath tp = new TreePath(path);
		this.annotationView.getTree().setSelectionPath(tp);
		this.annotationView.getTree().scrollPathToVisible(tp);

	}

}