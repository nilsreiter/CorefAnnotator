package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.kordamp.ikonli.material.Material;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public class FileSaveAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	DocumentWindow documentWindow;

	public FileSaveAction(DocumentWindow dw) {
		putValue(Action.NAME, Annotator.getString("action.save"));
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.SAVE));
		putValue(Action.SMALL_ICON, FontIcon.of(Material.SAVE));

		documentWindow = dw;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		documentWindow.saveCurrentFile();
	}

}
