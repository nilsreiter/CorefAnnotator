package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JTree;

import de.unistuttgart.ims.coref.annotator.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.TreeNode;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class DeleteMentionAction extends CRAction {

	private static final long serialVersionUID = 1L;

	JTree tree;

	public DeleteMentionAction(CoreferenceModel cm, JTree tree) {
		super(cm);
		this.tree = tree;
		putValue(Action.NAME, "Delete");

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		@SuppressWarnings("unchecked")
		TreeNode<Mention> tn = (TreeNode<Mention>) tree.getLastSelectedPathComponent();
		model.removeMention(tn.getFeatureStructure());
	}

}
