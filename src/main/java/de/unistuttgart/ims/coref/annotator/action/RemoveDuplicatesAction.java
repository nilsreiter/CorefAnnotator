package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.document.Op;

public class RemoveDuplicatesAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public RemoveDuplicatesAction() {
		putValue(Action.NAME, Annotator.getString("action.remove_duplicates"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DocumentWindow win = (DocumentWindow) SwingUtilities.getWindowAncestor((Component) e.getSource());
		MutableList<TreePath> paths = Lists.mutable.with(win.getTree().getSelectionPaths());

		win.getDocumentModel().getCoreferenceModel().edit(new Op.RemoveDuplicateMentionsInEntities(paths
				.collect(p -> (CATreeNode) p.getLastPathComponent()).collect(n -> (Entity) n.getFeatureStructure())));
	}

}
