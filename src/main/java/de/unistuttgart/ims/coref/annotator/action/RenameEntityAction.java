package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.TreeSelectionUtil;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateEntityName;

public class RenameEntityAction extends TargetedOperationIkonAction<DocumentWindow> implements TreeSelectionListener {

	private static final long serialVersionUID = 1L;

	public RenameEntityAction(DocumentWindow dw) {
		super(dw, Strings.ACTION_RENAME, MaterialDesign.MDI_RENAME_BOX);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_RENAME_TOOLTIP));
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		operationClass = UpdateEntityName.class;
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
	public void valueChanged(TreeSelectionEvent e) {
		TreeSelectionUtil tsu = new TreeSelectionUtil(e);
		setEnabled(tsu.isSingle() && tsu.isEntity());
	}

}