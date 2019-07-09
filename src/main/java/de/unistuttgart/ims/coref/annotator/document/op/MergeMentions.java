package de.unistuttgart.ims.coref.annotator.document.op;

import java.util.Collection;

import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.impl.factory.SortedSets;

import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.uima.AnnotationComparator;

public class MergeMentions implements CoreferenceModelOperation {
	ImmutableSortedSet<Mention> mentions;
	Mention newMention;

	public MergeMentions(Mention... mentions) {
		this.mentions = SortedSets.immutable.of(new AnnotationComparator(), mentions);
	}

	public MergeMentions(Collection<Mention> mentions) {
		this.mentions = SortedSets.immutable.withAll(new AnnotationComparator(), mentions);
	}

	public ImmutableSortedSet<Mention> getMentions() {
		return mentions;
	}

	public void setMentions(ImmutableSortedSet<Mention> mentions) {
		this.mentions = mentions;
	}

	public Mention getNewMention() {
		return newMention;
	}

	public void setNewMention(Mention newMention) {
		this.newMention = newMention;
	}

}
