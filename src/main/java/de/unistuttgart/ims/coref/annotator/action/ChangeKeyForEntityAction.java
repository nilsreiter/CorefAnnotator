package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JTree;

import de.unistuttgart.ims.coref.annotator.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.EntityTreeNode;

public class ChangeKeyForEntityAction extends CRAction {

	private static final long serialVersionUID = 1L;
	JTree tree;

	public ChangeKeyForEntityAction(CoreferenceModel cm, JTree tree) {
		super(cm);
		putValue(Action.NAME, "Shortcut");

		this.tree = tree;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String key = JOptionPane.showInputDialog("Enter the new key");
		EntityTreeNode etn = (EntityTreeNode) tree.getLastSelectedPathComponent();

		char keyCode = key.charAt(0);
		System.err.println(KeyEvent.getKeyText(keyCode) + " " + keyCode);
		model.reassignKey(keyCode, etn.getFeatureStructure());
	}

}
