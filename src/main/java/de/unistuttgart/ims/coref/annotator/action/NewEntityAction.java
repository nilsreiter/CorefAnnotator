package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

import de.unistuttgart.ims.coref.annotator.CoreferenceModel;

@Deprecated
public class NewEntityAction extends CRAction {

	private static final long serialVersionUID = 1L;

	JTextComponent tcomp;

	public NewEntityAction(CoreferenceModel cm, JTextComponent tc) {
		super(cm);
		tcomp = tc;
		putValue(Action.NAME, "New");
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		model.addNewEntityMention(tcomp.getSelectionStart(), tcomp.getSelectionEnd());
	}

}
