package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.plugin.IOPlugin;

public class FileImportAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	Annotator mainApplication;
	IOPlugin plugin;

	public FileImportAction(Annotator mApplication, IOPlugin plugin) {
		putValue(Action.NAME, plugin.getName());
		mainApplication = mApplication;
		this.plugin = plugin;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainApplication.fileOpenDialog(null, plugin);

	}
}
