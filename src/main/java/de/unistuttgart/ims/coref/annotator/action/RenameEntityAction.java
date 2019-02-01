package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CATreeSelectionListener;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public class RenameEntityAction extends DocumentWindowAction implements CAAction {

	private static final long serialVersionUID = 1L;

	public RenameEntityAction(DocumentWindow dw) {
		super(dw, Strings.ACTION_RENAME, MaterialDesign.MDI_RENAME_BOX);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_RENAME_TOOLTIP));
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				getTarget().getTree().startEditingAtPath(getTarget().getTree().getSelectionPath());
			}
		});
	}

	@Override
	public void setEnabled(CATreeSelectionListener l) {
		setEnabled(l.isSingle() && l.isEntity());
	}

}