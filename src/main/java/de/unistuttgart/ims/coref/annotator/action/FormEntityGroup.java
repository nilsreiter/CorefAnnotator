package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.TreeSelectionUtil;
import de.unistuttgart.ims.coref.annotator.document.op.GroupEntities;

public class FormEntityGroup extends TargetedOperationIkonAction<DocumentWindow> implements TreeSelectionListener {
	private static final long serialVersionUID = 1L;

	public FormEntityGroup(DocumentWindow documentWindow) {
		super(documentWindow, Strings.ACTION_GROUP, MaterialDesign.MDI_ACCOUNT_MULTIPLE);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_GROUP_TOOLTIP));
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		operationClass = GroupEntities.class;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().getDocumentModel().edit(new GroupEntities(getTarget().getSelectedEntities()));
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreeSelectionUtil tsu = new TreeSelectionUtil(e);
		setEnabled(tsu.size() > 1 && tsu.isEntity());
	}

}