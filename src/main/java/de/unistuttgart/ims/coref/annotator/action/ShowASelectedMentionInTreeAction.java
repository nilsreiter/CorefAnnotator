package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.tree.TreePath;

import org.eclipse.collections.api.set.MutableSet;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class ShowASelectedMentionInTreeAction extends DocumentWindowAction implements CaretListener {

	private static final long serialVersionUID = 1L;

	public ShowASelectedMentionInTreeAction(DocumentWindow documentWindow) {
		super(documentWindow, Strings.ACTION_SHOW_A_SELECTED_MENTION_IN_TREE, MaterialDesign.MDI_FILE_TREE);
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_SHOW_A_SELECTED_MENTION_IN_TREE_TOOLTIP));
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Mention toReveal = null;
		MutableSet<Mention> selectedMentions = getTarget().getTouchedAnnotations(Mention.class);
		if (selectedMentions.size() == 1) {
			toReveal = selectedMentions.getOnly();
		}
		if (toReveal != null) {
			Object[] path = getTarget().getDocumentModel().getTreeModel().getPathToRoot(toReveal);
			TreePath tp = new TreePath(path);
			this.getTarget().getTree().setSelectionPath(tp);
			this.getTarget().getTree().scrollPathToVisible(tp);
		}
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		setEnabled(getTarget().getTouchedAnnotations(Mention.class).size() == 1);
	}

}