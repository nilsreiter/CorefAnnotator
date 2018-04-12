package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.Defaults;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.worker.ExportWorker;

public class FileExportAction extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	IOPlugin plugin;

	public FileExportAction(DocumentWindow documentWindow, DocumentWindow dw, IOPlugin plugin) {
		super(dw, MaterialDesign.MDI_EXPORT);
		putValue(Action.NAME, plugin.getName());
		if (plugin.getDescription() != null)
			putValue(Action.SHORT_DESCRIPTION, plugin.getDescription());
		this.plugin = plugin;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser saveDialog = new JFileChooser(Annotator.app.getCurrentDirectory());
		saveDialog.setDialogType(JFileChooser.SAVE_DIALOG);
		saveDialog.setFileFilter(plugin.getFileFilter());
		saveDialog.setDialogTitle(Annotator.getString(Strings.DIALOG_EXPORT_AS_TITLE));
		saveDialog.setCurrentDirectory(Annotator.app.getCurrentDirectory());

		int r = saveDialog.showSaveDialog(target);

		if (r == JFileChooser.APPROVE_OPTION) {
			File f = saveDialog.getSelectedFile();
			if (!f.getName().endsWith(plugin.getSuffix())) {
				f = new File(f.getAbsolutePath() + plugin.getSuffix());
			}

			if (f.exists() && Annotator.app.getPreferences().getBoolean(Constants.CFG_ASK_BEFORE_FILE_OVERWRITE,
					Defaults.CFG_ASK_BEFORE_FILE_OVERWRITE)) {
				int answer = JOptionPane.showConfirmDialog(target,
						Annotator.getString(Constants.Strings.DIALOG_FILE_EXISTS_OVERWRITE));
				if (answer != JOptionPane.YES_OPTION) {
					return;
				}
			}
			target.setIndeterminateProgress();
			target.setMessage(Annotator.getString(Strings.MESSAGE_SAVING));

			Annotator.app.setCurrentDirectory(f.getParentFile());
			ExportWorker worker = new ExportWorker(f, target.getJCas(), plugin, (file, jcas) -> {
				target.stopIndeterminateProgress();
				target.setMessage("");
			});
			worker.execute();
		}
	}

}