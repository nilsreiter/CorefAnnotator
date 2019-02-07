package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CATreeSelectionEvent;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.document.op.GroupEntities;

public class FormEntityGroup extends DocumentWindowAction implements CAAction {
	private static final long serialVersionUID = 1L;

	public FormEntityGroup(DocumentWindow documentWindow) {
		super(documentWindow, Strings.ACTION_GROUP, MaterialDesign.MDI_ACCOUNT_MULTIPLE);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_GROUP_TOOLTIP));
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().getDocumentModel().edit(new GroupEntities(getTarget().getSelectedEntities()));
	}

	@Override
	public void setEnabled(CATreeSelectionEvent l) {
		setEnabled(l.isEntity() & l.size() > 1);
	}

}