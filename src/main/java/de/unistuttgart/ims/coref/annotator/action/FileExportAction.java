package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Defaults;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.plugins.ConfigurableExportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.UimaIOPlugin;
import de.unistuttgart.ims.coref.annotator.worker.ExportWorker;
import javafx.application.Platform;

public class FileExportAction extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	UimaIOPlugin plugin;

	public FileExportAction(DocumentWindow documentWindow, DocumentWindow dw, UimaIOPlugin plugin) {
		super(dw, plugin.getIkon());
		putValue(Action.NAME, plugin.getName());
		if (plugin.getDescription() != null)
			putValue(Action.SHORT_DESCRIPTION, plugin.getDescription());
		this.plugin = plugin;

	}

	protected void chooseFileAndSave() {
		if (Annotator.javafx()) {

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
					fileChooser.setTitle(Annotator.getString(Strings.DIALOG_EXPORT_AS_TITLE));
					fileChooser.setInitialDirectory(Annotator.app.getCurrentDirectory());
					fileChooser.getExtensionFilters().add(plugin.getExtensionFilter());
					File f = fileChooser.showSaveDialog(null);

					if (f != null) {
						if (!f.getName().endsWith(plugin.getSuffix())) {
							f = new File(f.getAbsolutePath() + plugin.getSuffix());
						}

						target.setIndeterminateProgress();
						target.setMessage(Annotator.getString(Strings.MESSAGE_SAVING));

						Annotator.app.setCurrentDirectory(f.getParentFile());
						ExportWorker worker = new ExportWorker(f, target.getDocumentModel().getJcas(), plugin,
								(file, jcas) -> {
									target.stopIndeterminateProgress();
									target.setMessage("");
								});
						worker.execute();
					}

				}

			});
		} else {
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
							Annotator.getString(Strings.DIALOG_FILE_EXISTS_OVERWRITE));
					if (answer != JOptionPane.YES_OPTION) {
						return;
					}
				}
				target.setIndeterminateProgress();
				target.setMessage(Annotator.getString(Strings.MESSAGE_SAVING));

				Annotator.app.setCurrentDirectory(f.getParentFile());
				ExportWorker worker = new ExportWorker(f, target.getDocumentModel().getJcas(), plugin, (file, jcas) -> {
					target.stopIndeterminateProgress();
					target.setMessage("");
				});
				worker.execute();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (plugin instanceof ConfigurableExportPlugin) {
			((ConfigurableExportPlugin) plugin).showExportConfigurationDialog(getTarget(),
					getTarget().getDocumentModel(), p -> chooseFileAndSave());
		} else {
			chooseFileAndSave();
		}

	}

}