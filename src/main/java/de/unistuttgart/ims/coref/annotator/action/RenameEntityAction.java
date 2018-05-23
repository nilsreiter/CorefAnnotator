package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CATreeSelectionListener;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.document.Op;

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
		Entity selectedEntity = getTarget().getSelectedEntities().getOnly();
		String l = selectedEntity.getLabel();
		String newLabel = (String) JOptionPane.showInputDialog(getTarget(),
				Annotator.getString(Strings.DIALOG_RENAME_ENTITY_PROMPT), "", JOptionPane.PLAIN_MESSAGE,
				FontIcon.of(MaterialDesign.MDI_KEYBOARD), null, l);
		if (newLabel != null) {
			Op.RenameEntity op = new Op.RenameEntity(selectedEntity, newLabel);
			getTarget().getCoreferenceModel().edit(op);
		}
	}

	@Override
	public void setEnabled(CATreeSelectionListener l) {
		setEnabled(l.isSingle() && l.isEntity());
	}

}