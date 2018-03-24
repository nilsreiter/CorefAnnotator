package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public class FileSaveAction extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	public FileSaveAction(DocumentWindow dw) {
		super(dw, MaterialDesign.MDI_CONTENT_SAVE);
		putValue(Action.NAME, Annotator.getString("action.save"));
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		target.saveCurrentFile();
	}

}
