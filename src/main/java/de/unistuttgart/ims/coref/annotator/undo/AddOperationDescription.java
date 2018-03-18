package de.unistuttgart.ims.coref.annotator.undo;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class AddOperationDescription implements EditOperationDescription {
	Mention mention;
	Span span;

	public AddOperationDescription(Span span) {
		this.span = span;
	}

	@Override
	public String toString() {
		return "add(" + mention.getBegin() + "," + mention.getEnd() + ")";
	}

	public Mention getMention() {
		return mention;
	}

	public void setMention(Mention mention) {
		this.mention = mention;
	}

	public Span getSpan() {
		return span;
	}

	public void setSpan(Span span) {
		this.span = span;
	}

}
