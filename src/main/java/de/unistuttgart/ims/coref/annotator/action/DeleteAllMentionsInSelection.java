package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Sets;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.document.op.RemoveMention;

public class DeleteAllMentionsInSelection extends TargetedIkonAction<DocumentWindow> implements CaretListener {

	private static final long serialVersionUID = 1L;

	public DeleteAllMentionsInSelection(DocumentWindow documentWindow) {
		super(documentWindow, Strings.ACTION_DELETE_IN_SELECTION, MaterialDesign.MDI_DELETE);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_DELETE_IN_SELECTION_TOOLTIP));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int low = getTarget().getTextPane().getSelectionStart();
		int high = getTarget().getTextPane().getSelectionEnd();
		if (low == high)
			return;
		MutableSet<Mention> annotations = Sets.mutable
				.withAll(getTarget().getDocumentModel().getCoreferenceModel().getMentionsBetween(low, high));
		MutableSet<Mention> mentions = annotations.select(a -> a instanceof Mention);

		mentions.groupBy(m -> m.getEntity())
				.forEachKeyMultiValues((entity, ms) -> getTarget().getDocumentModel().edit((new RemoveMention(ms))));

	}

	@Override
	public void caretUpdate(CaretEvent e) {
		setEnabled(e.getDot() != e.getMark());
	}

}