package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.document.op.AddMentionsToNewEntity;

public class NewEntityAction extends TargetedOperationIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	public NewEntityAction(DocumentWindow dw) {
		super(dw, Strings.ACTION_NEW, MaterialDesign.MDI_ACCOUNT_PLUS);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_NEW_TOOLTIP));
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

		operationClass = AddMentionsToNewEntity.class;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		AddMentionsToNewEntity op = new AddMentionsToNewEntity(getTarget().getSelection());
		getTarget().getDocumentModel().edit(op);
	}

}