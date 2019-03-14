package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.worker.SaveJCasWorker;
import javafx.application.Platform;

public class FileSaveAsAction extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	public FileSaveAsAction(DocumentWindow documentWindow) {
		super(documentWindow, Strings.ACTION_SAVE_AS, MaterialDesign.MDI_CONTENT_SAVE_SETTINGS);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | KeyEvent.SHIFT_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
				fileChooser.setTitle(Annotator.getString(Strings.DIALOG_SAVE_AS_TITLE));
				fileChooser.setInitialDirectory(Annotator.app.getCurrentDirectory());
				fileChooser.getExtensionFilters()
						.add(Annotator.app.getPluginManager().getDefaultIOPlugin().getExtensionFilter());
				File f = fileChooser.showSaveDialog(null);

				if (f != null) {
					if (!f.getName().endsWith(".xmi") && !f.getName().endsWith(".xmi.gz"))
						f = new File(f.getAbsolutePath() + ".xmi.gz");

					SaveJCasWorker worker = new SaveJCasWorker(f, target.getDocumentModel().getJcas(),
							SaveJCasWorker.getConsumer(getTarget()));
					worker.execute();
				}

			}

		});

	}
}