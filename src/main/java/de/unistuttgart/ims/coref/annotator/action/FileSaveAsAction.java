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
import de.unistuttgart.ims.coref.annotator.AnnotationView;

public class FileSaveAsAction extends TargetedIkonAction<AnnotationView> {

	private static final long serialVersionUID = 1L;

	public FileSaveAsAction(AnnotationView annotationView) {
		super(null, Strings.ACTION_SAVE_AS, MaterialDesign.MDI_CONTENT_SAVE_SETTINGS);
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
		int r = saveDialog.showSaveDialog(getTarget());
		switch (r) {
		case JFileChooser.APPROVE_OPTION:
			File f = saveDialog.getSelectedFile();
			if (!f.getName().endsWith(".xmi")) {
				f = new File(f.getAbsolutePath() + ".xmi");
			}
			getTarget().saveToFile(f, Annotator.app.getPluginManager().getDefaultIOPlugin(), true);
			Annotator.app.recentFiles.add(0, f);
			Annotator.app.refreshRecents();
			break;
		default:
		}

	}
}