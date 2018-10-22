package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.document.op.AddMentionsToEntity;

public class AddCurrentSpanToCurrentEntity extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	public AddCurrentSpanToCurrentEntity(DocumentWindow dw) {
		super(dw, MaterialDesign.MDI_CHECKBOX_BLANK_CIRCLE);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		int b = getTarget().getTextPane().getSelectionStart(), e = getTarget().getTextPane().getSelectionEnd();
		if (b != e) {
			for (TreePath tp : getTarget().getTree().getSelectionPaths()) {
				if (((CATreeNode) tp.getLastPathComponent()).isEntity()) {
					CATreeNode etn = (CATreeNode) tp.getLastPathComponent();
					getTarget().getDocumentModel().edit(new AddMentionsToEntity(etn.getEntity(), new Span(b, e)));
				}
			}
			getTarget().getTextPane().requestFocusInWindow();
			getTarget().getTreeSearchField().setText("");
		}
	}

}
