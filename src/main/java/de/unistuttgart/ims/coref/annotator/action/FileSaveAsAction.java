package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.function.BiConsumer;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.apache.uima.jcas.JCas;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.ExtensionFilters;
import de.unistuttgart.ims.coref.annotator.FileFilters;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.worker.SaveJCasWorker;
import javafx.application.Platform;

public class FileSaveAsAction extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	boolean closeAfterSaving = false;

	public FileSaveAsAction(DocumentWindow documentWindow) {
		super(documentWindow, Strings.ACTION_SAVE_AS, MaterialDesign.MDI_CONTENT_SAVE_SETTINGS);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | KeyEvent.SHIFT_DOWN_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (Annotator.javafx()) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
					fileChooser.setTitle(Annotator.getString(Strings.DIALOG_SAVE_AS_TITLE));
					fileChooser.setInitialDirectory(Annotator.app.getCurrentDirectory());
					fileChooser.getExtensionFilters().add(ExtensionFilters.ca2);
					fileChooser.setInitialFileName(getTarget().getProposedFilename());
					File f = fileChooser.showSaveDialog(null);

					if (f != null) {
						if (!(f.getName().endsWith(".ca2")) && !(f.getName().endsWith(".ca2z")))
							f = new File(f.getAbsolutePath() + ".ca2z");

						BiConsumer<File, JCas> bicons;
						if (closeAfterSaving) {
							bicons = (file, jcas) -> {
								SaveJCasWorker.getConsumer(getTarget()).accept(file, jcas);
								getTarget().closeWindow(false);
							};
						} else {
							bicons = SaveJCasWorker.getConsumer(getTarget());
						}

						SaveJCasWorker worker = new SaveJCasWorker(f, target.getDocumentModel().getJcas(), bicons);
						worker.execute();
					}

				}

			});
		} else {
			JFileChooser saveDialog;
			if (this.getTarget().getFile() == null)
				saveDialog = new JFileChooser();
			else
				saveDialog = new JFileChooser(getTarget().getFile().getParentFile());
			saveDialog.setDialogType(JFileChooser.SAVE_DIALOG);
			saveDialog.setFileFilter(new FileFilter() {

				@Override
				public boolean accept(File f) {
					return FileFilters.ca2.accept(f);
				}

				@Override
				public String getDescription() {
					return FileFilters.ca2.getDescription();
				}

			});
			saveDialog.setDialogTitle(Annotator.getString(Strings.DIALOG_SAVE_AS_TITLE));
			saveDialog.setCurrentDirectory(Annotator.app.getCurrentDirectory());
			if (getTarget().getProposedFilename() != null) {
				File proposedFile = new File(Annotator.app.getCurrentDirectory(),
						getTarget().getProposedFilename() + ".ca2z");
				saveDialog.setSelectedFile(proposedFile);
			}
			int r = saveDialog.showSaveDialog(getTarget());
			switch (r) {
			case JFileChooser.APPROVE_OPTION:
				File f = saveDialog.getSelectedFile();
				if (!(f.getName().endsWith(".ca2")) && !(f.getName().endsWith(".ca2z")))
					f = new File(f.getAbsolutePath() + ".ca2z");

				SaveJCasWorker worker = new SaveJCasWorker(f, target.getDocumentModel().getJcas(),
						SaveJCasWorker.getConsumer(getTarget()));
				worker.execute();
				break;
			default:
			}
		}

	}

	/**
	 * @return the closeAfterSaving
	 */
	public boolean isCloseAfterSaving() {
		return closeAfterSaving;
	}

	/**
	 * @param closeAfterSaving the closeAfterSaving to set
	 */
	public void setCloseAfterSaving(boolean closeAfterSaving) {
		this.closeAfterSaving = closeAfterSaving;
	}
}