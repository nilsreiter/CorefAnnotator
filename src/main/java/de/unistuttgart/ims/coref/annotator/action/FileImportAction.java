package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.material.Material;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;

public class FileImportAction extends AnnotatorAction {
	private static final long serialVersionUID = 1L;

	IOPlugin plugin;

	public FileImportAction(Annotator mApplication, IOPlugin plugin) {
		super(mApplication, Material.INPUT);
		putValue(Action.NAME, plugin.getName());
		putValue(Action.SHORT_DESCRIPTION, plugin.getDescription());
		this.plugin = plugin;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainApplication.fileOpenDialog(null, plugin);

	}
}
