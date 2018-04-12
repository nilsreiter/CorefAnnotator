package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.worker.SaveJCasWorker;

public class FileSaveAsAction extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	public FileSaveAsAction(DocumentWindow documentWindow) {
		super(documentWindow, Strings.ACTION_SAVE_AS, MaterialDesign.MDI_CONTENT_SAVE_SETTINGS);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | KeyEvent.SHIFT_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser saveDialog;
		if (this.getTarget().getFile() == null)
			saveDialog = new JFileChooser();
		else
			saveDialog = new JFileChooser(getTarget().getFile().getParentFile());
		saveDialog.setDialogType(JFileChooser.SAVE_DIALOG);
		saveDialog.setFileFilter(Annotator.app.getPluginManager().getDefaultIOPlugin().getFileFilter());
		saveDialog.setDialogTitle(Annotator.getString(Strings.DIALOG_SAVE_AS_TITLE));
		saveDialog.setCurrentDirectory(Annotator.app.getCurrentDirectory());
		int r = saveDialog.showSaveDialog(getTarget());
		switch (r) {
		case JFileChooser.APPROVE_OPTION:
			File f = saveDialog.getSelectedFile();
			if (!f.getName().endsWith(".xmi")) {
				f = new File(f.getAbsolutePath() + ".xmi");
			}

			SaveJCasWorker worker = new SaveJCasWorker(f, target.getJCas(), SaveJCasWorker.getConsumer(getTarget()));
			worker.execute();
			break;
		default:
		}

	}
}