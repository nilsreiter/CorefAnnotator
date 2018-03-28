package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JFileChooser;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;

public class FileExportAction extends TargetedIkonAction<DocumentWindow> {
	/**
	 * 
	 */
	private final DocumentWindow documentWindow;

	private static final long serialVersionUID = 1L;

	IOPlugin plugin;

	public FileExportAction(DocumentWindow documentWindow, DocumentWindow dw, IOPlugin plugin) {
		super(dw, MaterialDesign.MDI_EXPORT);
		this.documentWindow = documentWindow;
		putValue(Action.NAME, plugin.getName());
		this.plugin = plugin;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser saveDialog = new JFileChooser(Annotator.app.getCurrentDirectory());
		saveDialog.setDialogType(JFileChooser.SAVE_DIALOG);
		saveDialog.setFileFilter(plugin.getFileFilter());
		saveDialog.setDialogTitle(Annotator.getString(Strings.DIALOG_EXPORT_AS_TITLE));
		saveDialog.setCurrentDirectory(Annotator.app.getCurrentDirectory());
		int r = saveDialog.showSaveDialog(this.documentWindow);
		switch (r) {
		case JFileChooser.APPROVE_OPTION:
			File f = saveDialog.getSelectedFile();
			Annotator.app.setCurrentDirectory(f.getParentFile());
			getTarget().saveToFile(f, plugin, true);
			break;
		default:
		}
	}

}