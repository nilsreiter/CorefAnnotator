package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.kordamp.ikonli.material.Material;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;

public class FileImportAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	Annotator mainApplication;
	IOPlugin plugin;

	public FileImportAction(Annotator mApplication, IOPlugin plugin) {
		putValue(Action.NAME, plugin.getName());
		putValue(Action.SHORT_DESCRIPTION, plugin.getDescription());
		putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.INPUT));
		putValue(Action.SMALL_ICON, FontIcon.of(Material.INPUT));
		this.mainApplication = mApplication;
		this.plugin = plugin;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainApplication.fileOpenDialog(null, plugin);

	}
}
