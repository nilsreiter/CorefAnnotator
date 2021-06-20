package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.impl.factory.SortedSets;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.api.v2.Mention;

public class MergeAllAdjacentMentions extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	public MergeAllAdjacentMentions(DocumentWindow dw) {
		super(dw, Strings.ACTION_MERGE_ADJACENT_MENTIONS, MaterialDesign.MDI_CALL_MERGE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JCas jcas = getTarget().getJCas();

		ImmutableSortedSet<Mention> mentions = SortedSets.immutable.withAll(jcas.getIndexedFSs(Mention.class));

		Iterator<Mention> mentionIterator = mentions.iterator();

		Mention current = null, previous = null;
		while (mentionIterator.hasNext()) {
			previous = current;
			current = mentionIterator.next();
			if (previous == null)
				continue;
		}
	}

}
