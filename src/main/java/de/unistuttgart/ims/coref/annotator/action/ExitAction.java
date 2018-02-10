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

public class ExitAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public ExitAction() {
		putValue(Action.NAME, Annotator.getString("action.quit"));
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.CLOSE));
		putValue(Action.SMALL_ICON, FontIcon.of(Material.CLOSE));

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Annotator.app.handleQuitRequestWith(null, null);

	}
}
