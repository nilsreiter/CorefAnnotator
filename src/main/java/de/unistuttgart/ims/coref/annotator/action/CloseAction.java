package de.unistuttgart.ims.coref.annotator.action;

import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;

public class CloseAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public CloseAction() {
		super(Annotator.getString(Constants.Strings.ACTION_CLOSE));
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Find the active window before creating and dispatching the event

		Window window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();

		if (window != null) {
			WindowEvent windowClosing = new WindowEvent(window, WindowEvent.WINDOW_CLOSING);
			window.dispatchEvent(windowClosing);
		}
	}
}
