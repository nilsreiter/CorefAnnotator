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

public class FileOpenAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	Annotator mainApplication;

	public FileOpenAction(Annotator mApplication) {
		putValue(Action.NAME, Annotator.getString("action.open"));
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.FOLDER_OPEN));
		putValue(Action.SMALL_ICON, FontIcon.of(Material.FOLDER_OPEN));
		mainApplication = mApplication;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainApplication.fileOpenDialog(null, mainApplication.getPluginManager().getDefaultIOPlugin());

	}
}
