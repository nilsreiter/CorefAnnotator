package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;

public class ExitAction extends IkonAction {
	private static final long serialVersionUID = 1L;

	public ExitAction() {
		super(MaterialDesign.MDI_CLOSE);
		putValue(Action.NAME, Annotator.getString("action.quit"));
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Annotator.app.handleQuitRequestWith(null, null);

	}
}
