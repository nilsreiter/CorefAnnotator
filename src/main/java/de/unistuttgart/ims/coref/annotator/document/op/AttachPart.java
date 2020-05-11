package de.unistuttgart.ims.coref.annotator.document.op;

import de.unistuttgart.ims.coref.annotator.Span;
import  de.unistuttgart.ims.coref.annotator.api.v2.DetachedMentionPart;
import  de.unistuttgart.ims.coref.annotator.api.v2.Mention;

public class AttachPart implements CoreferenceModelOperation {
	Mention mention;
	DetachedMentionPart part;
	Span span;

	public AttachPart(Mention mention, Span span) {
		this.mention = mention;
		this.span = span;
	}

	public Mention getMention() {
		return mention;
	}

	public DetachedMentionPart getPart() {
		return part;
	}

	public Span getSpan() {
		return span;
	}

	public void setMention(Mention mention) {
		this.mention = mention;
	}

	public void setPart(DetachedMentionPart part) {
		this.part = part;
	}

	public void setSpan(Span span) {
		this.span = span;
	}
}