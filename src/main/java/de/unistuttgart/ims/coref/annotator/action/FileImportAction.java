package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.plugins.ImportPlugin;

public class FileImportAction extends IkonAction {
	private static final long serialVersionUID = 1L;

	ImportPlugin plugin;

	public FileImportAction(Annotator mApplication, ImportPlugin plugin) {
		super(plugin.getName(), false, plugin.getIkon());
		putValue(Action.SHORT_DESCRIPTION, plugin.getDescription());
		this.plugin = plugin;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Annotator.app.fileOpenDialog(null, plugin);

	}
}
