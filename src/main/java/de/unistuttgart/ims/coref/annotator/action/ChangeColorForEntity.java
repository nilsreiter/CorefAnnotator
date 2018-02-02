package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JColorChooser;
import javax.swing.JTree;
import javax.swing.KeyStroke;

import de.unistuttgart.ims.coref.annotator.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.EntityTreeNode;

public class ChangeColorForEntity extends CRAction {

	private static final long serialVersionUID = 1L;
	JTree tree;

	public ChangeColorForEntity(CoreferenceModel cm, JTree tree) {
		super(cm);
		putValue(Action.NAME, "Color");
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		this.tree = tree;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		EntityTreeNode etn = (EntityTreeNode) tree.getLastSelectedPathComponent();
		Color color = new Color(etn.getFeatureStructure().getColor());

		Color newColor = JColorChooser.showDialog(null, "Choose new color", color);
		model.updateColor(etn.getFeatureStructure(), newColor);
	}

}
