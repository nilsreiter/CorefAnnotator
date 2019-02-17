package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.api.v1.MentionExtent;

public class AddMentionExtent implements CoreferenceModelOperation {
	Mention mention;

	ImmutableList<Span> spans;

	ImmutableList<MentionExtent> mentionExtent;

	public AddMentionExtent(Mention mention, Iterable<Span> mentionExtents) {
		this.mention = mention;
		this.spans = Lists.immutable.withAll(mentionExtents);
	}

	public AddMentionExtent(Mention mention, Span... mentionExtents) {
		this.mention = mention;
		this.spans = Lists.immutable.of(mentionExtents);
	}

	public Mention getMention() {
		return mention;
	}

	public void setMention(Mention mention) {
		this.mention = mention;
	}

	public ImmutableList<Span> getSpans() {
		return spans;
	}

	public void setSpans(ImmutableList<Span> mentionExtents) {
		this.spans = mentionExtents;
	}

	public ImmutableList<MentionExtent> getMentionExtent() {
		return mentionExtent;
	}

	public void setMentionExtent(ImmutableList<MentionExtent> mentionExtent) {
		this.mentionExtent = mentionExtent;
	}
}
