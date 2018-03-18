package de.unistuttgart.ims.coref.annotator.undo;

import de.unistuttgart.ims.coref.annotator.api.Mention;

public class AddOperationDescription implements EditOperationDescription {
	Mention mention;

	public AddOperationDescription(Mention span) {
		this.mention = span;
	}

	@Override
	public String toString() {
		return "add(" + mention.getBegin() + "," + mention.getEnd() + ")";
	}

	public Mention getMention() {
		return mention;
	}

}
