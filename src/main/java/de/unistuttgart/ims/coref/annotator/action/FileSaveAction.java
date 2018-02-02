package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public class FileSaveAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	DocumentWindow documentWindow;

	public FileSaveAction(DocumentWindow dw) {
		putValue(Action.NAME, "Save");
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		documentWindow = dw;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		documentWindow.saveCurrentFile();
	}

}
