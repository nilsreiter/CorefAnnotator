package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.tree.TreePath;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.document.Op;

public class RemoveDuplicatesAction extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	public RemoveDuplicatesAction(DocumentWindow dw) {
		super(dw, MaterialDesign.MDI_BEACH);
		putValue(Action.NAME, Annotator.getString("action.remove_duplicates"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MutableList<TreePath> paths = Lists.mutable.with(getTarget().getTree().getSelectionPaths());

		getTarget().getDocumentModel().getCoreferenceModel().edit(new Op.RemoveDuplicateMentionsInEntities(paths
				.collect(p -> (CATreeNode) p.getLastPathComponent()).collect(n -> (Entity) n.getFeatureStructure())));
	}

}
