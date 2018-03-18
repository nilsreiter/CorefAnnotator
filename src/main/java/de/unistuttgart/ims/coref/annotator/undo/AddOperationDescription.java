package de.unistuttgart.ims.coref.annotator.undo;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.Mention;

@Deprecated
public class AddOperationDescription extends BatchAddOperationDescription implements EditOperationDescription {
	Mention mention;

	public AddOperationDescription(Span span) {
		super(span);
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
		return spans.getFirst();
	}

}
