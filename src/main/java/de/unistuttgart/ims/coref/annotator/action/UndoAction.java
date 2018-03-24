package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.AnnotationView;

public class UndoAction extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	public UndoAction(AnnotationView dw) {
		super(dw, Constants.Strings.ACTION_UNDO, MaterialDesign.MDI_UNDO);
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		annotationView.getDocumentModel().getCoreferenceModel().undo();
		// TODO: working mechanism to disable the button
		// setEnabled(documentWindow.getCoreferenceModel().getHistory().size() >
		// 0);
	}

}
