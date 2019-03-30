package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.Defaults;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.worker.ExportWorker;
import javafx.application.Platform;

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
					ExportWorker worker = new ExportWorker(f, target.getDocumentModel().getJcas(), plugin,
							(file, jcas) -> {
								target.stopIndeterminateProgress();
								target.setMessage("");
							});
					worker.execute();
				}

			}

		});

	}

}