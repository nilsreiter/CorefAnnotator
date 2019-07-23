package de.unistuttgart.ims.coref.annotator.document.op;

import java.util.Collection;
import java.util.function.Function;

import org.apache.uima.cas.FeatureStructure;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.impl.factory.SortedSets;

import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.uima.AnnotationComparator;

public class MergeMentions implements CoreferenceModelOperation {
	public static final int STATE_OK = 0;
	public static final int STATE_NOT_ADJACENT = 1;
	public static final int STATE_NOT_TWO = 2;
	public static final int STATE_NOT_MENTIONS = 3;
	public static final int STATE_NOT_SAME_ENTITY = 4;

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

	public static Function<ImmutableSet<? extends FeatureStructure>, Integer> isApplicable() {
		return (ms -> {
			if (ms.size() != 2)
				return STATE_NOT_TWO;
			if (!ms.allSatisfy(o -> o instanceof Mention))
				return STATE_NOT_MENTIONS;

			ImmutableSortedSet<Mention> sms = ms.selectInstancesOf(Mention.class)
					.toSortedSet(new AnnotationComparator()).toImmutable();

			Mention m1 = sms.getFirstOptional().get();
			Mention m2 = sms.getLastOptional().get();

			if (m1.getEntity() != m2.getEntity())
				return STATE_NOT_SAME_ENTITY;

			if (m1.getEnd() == m2.getBegin())
				return STATE_OK;

			String between = m1.getCAS().getDocumentText().substring(m1.getEnd(), m2.getBegin());
			if (!between.matches("^\\p{Space}*$"))
				return STATE_NOT_ADJACENT;

			return STATE_OK;
		});
	}

}