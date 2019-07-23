package de.unistuttgart.ims.coref.annotator.document;

import java.util.Collection;
import java.util.function.Function;

import org.apache.uima.cas.FeatureStructure;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.impl.factory.SortedSets;

import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.api.v1.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.document.Event.Type;
import de.unistuttgart.ims.coref.annotator.document.op.CoreferenceModelOperation;
import de.unistuttgart.ims.coref.annotator.document.op.Operation2;
import de.unistuttgart.ims.coref.annotator.uima.AnnotationComparator;

public class MergeMentions implements CoreferenceModelOperation, Operation2<CoreferenceModel> {
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

	@Override
	public String getUIKey() {
		return Strings.ACTION_MERGE_ADJACENT_MENTIONS;
	}

	@Override
	public int isApplicable(CoreferenceModel model) {
		if (mentions.size() != 2)
			return STATE_NOT_TWO;
		if (!mentions.allSatisfy(o -> o instanceof Mention))
			return STATE_NOT_MENTIONS;

		ImmutableSortedSet<Mention> sms = mentions.selectInstancesOf(Mention.class)
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
	}

	@Override
	public Mention edit(CoreferenceModel model) {
		Mention firstMention = getMentions().getFirstOptional().get();
		int begin = getMentions().getFirstOptional().get().getBegin();
		int end = getMentions().getLastOptional().get().getEnd();
		Mention newMention = model.addTo(firstMention.getEntity(), begin, end);

		getMentions().forEach(m -> {
			model.remove(m, false);
			if (m.getDiscontinuous() != null) {
				DetachedMentionPart dmp = m.getDiscontinuous();
				model.remove(dmp);
				model.fireEvent(Event.get(model, Event.Type.Remove, m, dmp));
			}
		});
		model.fireEvent(Event.get(model, Event.Type.Remove, firstMention.getEntity(), getMentions()));
		model.fireEvent(Event.get(model, Event.Type.Add, newMention.getEntity(), newMention));
		setNewMention(newMention);
		model.registerEdit(this);
		return newMention;
	}

	@Override
	public Object undo(CoreferenceModel model) {
		getMentions().forEach(m -> {
			m.addToIndexes();
			model.entityMentionMap.put(getNewMention().getEntity(), m);
			model.fireEvent(Event.get(model, Event.Type.Add, m.getEntity(), m));
		});
		model.remove(getNewMention(), false);
		model.fireEvent(Event.get(model, Type.Remove, getNewMention().getEntity(), getNewMention()));
		return null;
	}

}
