package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;

public class FileSelectOpenAction extends IkonAction {

	private static final long serialVersionUID = 1L;

	Annotator mainApplication;

	public FileSelectOpenAction(Annotator mApplication) {
		super(MaterialDesign.MDI_OPEN_IN_APP);
		putValue(Action.NAME, Annotator.getString("action.open"));
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		mainApplication = mApplication;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainApplication.fileOpenDialog(null, mainApplication.getPluginManager().getDefaultIOPlugin());

	}
}
