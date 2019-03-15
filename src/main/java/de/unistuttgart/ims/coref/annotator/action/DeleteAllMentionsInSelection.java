package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Sets;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CAAbstractTreeSelectionListener;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.document.op.RemoveMention;

public class DeleteAllMentionsInSelection extends TargetedIkonAction<DocumentWindow> implements CAAction {

	private static final long serialVersionUID = 1L;

	public DeleteAllMentionsInSelection(DocumentWindow documentWindow) {
		super(documentWindow, Strings.ACTION_DELETE, MaterialDesign.MDI_DELETE);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_DELETE_TOOLTIP));
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int low = getTarget().getTextPane().getSelectionStart();
		int high = getTarget().getTextPane().getSelectionEnd();
		MutableSet<? extends Annotation> annotations = Sets.mutable
				.withAll(getTarget().getDocumentModel().getCoreferenceModel().getMentions(low, high));
		@SuppressWarnings("unchecked")
		MutableSet<Mention> mentions = (MutableSet<Mention>) annotations.select(a -> a instanceof Mention);

		mentions.groupBy(m -> m.getEntity())
				.forEachKeyMultiValues((entity, ms) -> getTarget().getDocumentModel().edit((new RemoveMention(ms))));

	}

	@Override
	public void setEnabled(CAAbstractTreeSelectionListener l) {
		setEnabled(l.isDetachedMentionPart() || l.isMention() || (l.isEntityGroup() && l.isLeaf())
				|| (l.isEntity() && l.isLeaf()));

	}

}