package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JFileChooser;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.AnnotationView;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;

public class FileExportAction extends TargetedIkonAction<AnnotationView> {
	/**
	 * 
	 */
	private final AnnotationView annotationView;

	private static final long serialVersionUID = 1L;

	IOPlugin plugin;

	public FileExportAction(AnnotationView annotationView, AnnotationView dw, IOPlugin plugin) {
		super(dw, MaterialDesign.MDI_EXPORT);
		this.annotationView = annotationView;
		putValue(Action.NAME, plugin.getName());
		this.plugin = plugin;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser saveDialog = new JFileChooser(getTarget().getFile().getParentFile());
		saveDialog.setDialogType(JFileChooser.SAVE_DIALOG);
		saveDialog.setFileFilter(plugin.getFileFilter());
		saveDialog.setDialogTitle(Annotator.getString(Strings.DIALOG_EXPORT_AS_TITLE));
		int r = saveDialog.showSaveDialog(this.annotationView);
		switch (r) {
		case JFileChooser.APPROVE_OPTION:
			File f = saveDialog.getSelectedFile();
			getTarget().saveToFile(f, plugin, true);
			break;
		default:
		}
	}

}