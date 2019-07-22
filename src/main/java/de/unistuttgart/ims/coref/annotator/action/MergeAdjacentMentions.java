package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.TreeSelectionUtil;
import de.unistuttgart.ims.coref.annotator.document.op.MergeMentions;

public class MergeAdjacentMentions extends TargetedIkonAction<DocumentWindow> implements TreeSelectionListener {

	private static final long serialVersionUID = 1L;

	public MergeAdjacentMentions(DocumentWindow dw) {
		super(dw, Strings.ACTION_MERGE_ADJACENT_MENTIONS, MaterialDesign.MDI_CALL_MERGE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MergeMentions op = new MergeMentions(getTarget().getSelectedMentions().castToCollection());
		getTarget().getDocumentModel().edit(op);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreeSelectionUtil tsu = new TreeSelectionUtil(e);

		int applicable = MergeMentions.isApplicable().apply(tsu.getFeatureStructures().toSet().toImmutable());

		switch (applicable) {
		case MergeMentions.STATE_NOT_ADJACENT:
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_MERGE_ADJACENT_MENTIONS_TOOLTIP));
			break;
		case MergeMentions.STATE_NOT_MENTIONS:
			putValue(Action.SHORT_DESCRIPTION,
					Annotator.getString(Strings.ACTION_MERGE_ADJACENT_MENTIONS_UNABLE_NOT_TWO_MENTIONS));
			break;
		case MergeMentions.STATE_NOT_SAME_ENTITY:
			break;
		case MergeMentions.STATE_OK:
			putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_MERGE_ADJACENT_MENTIONS_TOOLTIP));
			break;
		}

		setEnabled(applicable == 0);
	}

}
