package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.api.v2.MentionSurface;

public class RemoveMentionSurface implements CoreferenceModelOperation {
	ImmutableList<Mention> mention;
	ImmutableList<MentionSurface> mentionSurface;
	ImmutableList<Span> spans;

	public RemoveMentionSurface(ImmutableList<MentionSurface> mentionSurface) {
		super();
		this.mentionSurface = mentionSurface;
	}

	public RemoveMentionSurface(MentionSurface... mentionSurface) {
		super();
		this.mentionSurface = Lists.immutable.of(mentionSurface);
	}

	/**
	 * @return the mentionSurface
	 */
	public ImmutableList<MentionSurface> getMentionSurface() {
		return mentionSurface;
	}

	/**
	 * @param mentionSurface the mentionSurface to set
	 */
	public void setMentionSurface(ImmutableList<MentionSurface> mentionSurface) {
		this.mentionSurface = mentionSurface;
	}

	/**
	 * @return the spans
	 */
	public ImmutableList<Span> getSpans() {
		return spans;
	}

	public Span getSpan(int i) {
		return spans.get(i);
	}

	/**
	 * @param spans the spans to set
	 */
	public void setSpans(ImmutableList<Span> spans) {
		this.spans = spans;
	}

	/**
	 * @return the mention
	 */
	public ImmutableList<Mention> getMention() {
		return mention;
	}

	public Mention getMention(int i) {
		return mention.get(i);
	}

	/**
	 * @param mention the mention to set
	 */
	public void setMention(ImmutableList<Mention> mention) {
		this.mention = mention;
	}

}
