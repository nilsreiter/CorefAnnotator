package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.plugins.UimaIOPlugin;

public class FileImportAction extends IkonAction {
	private static final long serialVersionUID = 1L;

	UimaIOPlugin plugin;

	public FileImportAction(Annotator mApplication, UimaIOPlugin plugin) {
		super(plugin.getName(), false, plugin.getIkon());
		putValue(Action.SHORT_DESCRIPTION, plugin.getDescription());
		this.plugin = plugin;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Annotator.app.fileOpenDialog(null, plugin);

	}
}
