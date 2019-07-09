package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.impl.factory.SortedSets;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.TreeSelectionUtil;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.document.op.MergeMentions;
import de.unistuttgart.ims.coref.annotator.uima.AnnotationComparator;

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

		if (!(tsu.isMention() && tsu.isDouble())) {
			setEnabled(false);
			return;
		}
		boolean areAdjacent = false;
		ImmutableSortedSet<Mention> mentions = SortedSets.immutable.ofAll(new AnnotationComparator(),
				tsu.getFeatureStructures().selectInstancesOf(Mention.class));
		int firstEnd = mentions.getFirstOptional().get().getEnd();
		int secondBegin = mentions.getLastOptional().get().getBegin();

		if (firstEnd == secondBegin)
			areAdjacent = true;
		else if (firstEnd < secondBegin) {
			String between = getTarget().getJCas().getDocumentText().substring(firstEnd, secondBegin);
			areAdjacent = between.matches("^\\p{Space}*$");
		}
		setEnabled(areAdjacent && tsu.isMention() && tsu.isDouble());
	}

}
