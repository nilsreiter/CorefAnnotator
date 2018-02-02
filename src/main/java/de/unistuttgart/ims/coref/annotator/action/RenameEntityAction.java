package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;

import de.unistuttgart.ims.coref.annotator.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.EntityTreeNode;

public class RenameEntityAction extends CRAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTree tree;

	public RenameEntityAction(CoreferenceModel cm, JTree tree) {
		super(cm);
		putValue(Action.NAME, "Rename");
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		this.tree = tree;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String name = JOptionPane.showInputDialog("Enter the new name:");
		EntityTreeNode etn = (EntityTreeNode) tree.getLastSelectedPathComponent();
		etn.getFeatureStructure().setLabel(name);
		model.nodeChanged(etn);
	}

}
