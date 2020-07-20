package de.unistuttgart.ims.coref.annotator.document.op;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.api.v2.MentionSurface;

public class AddSpanToMention implements CoreferenceModelOperation {
	Mention target;
	Span span;
	MentionSurface mentionSurface;

	public AddSpanToMention(Mention target, Span span) {
		super();
		this.target = target;
		this.span = span;
	}

	/**
	 * @return the target
	 */
	public Mention getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(Mention target) {
		this.target = target;
	}

	/**
	 * @return the span
	 */
	public Span getSpan() {
		return span;
	}

	/**
	 * @param span the span to set
	 */
	public void setSpan(Span span) {
		this.span = span;
	}

	/**
	 * @return the mentionSurface
	 */
	public MentionSurface getMentionSurface() {
		return mentionSurface;
	}

	/**
	 * @param mentionSurface the mentionSurface to set
	 */
	public void setMentionSurface(MentionSurface mentionSurface) {
		this.mentionSurface = mentionSurface;
	}
}
