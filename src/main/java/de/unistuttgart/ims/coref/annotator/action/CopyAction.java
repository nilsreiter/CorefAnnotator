package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public class CopyAction extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	public CopyAction(DocumentWindow dw) {
		super(dw, Constants.Strings.ACTION_COPY, MaterialDesign.MDI_CONTENT_COPY);
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new StringSelection(documentWindow.getTextPane().getSelectedText()), null);
	}

}
