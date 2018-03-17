package de.unistuttgart.ims.coref.annotator.undo;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;

public class AddToOperation extends AbstractEditOperation {
	Entity entity;
	Mention mention;
	Span span;

	public AddToOperation(Entity entity, Span span) {
		this.entity = entity;
		this.span = span;
	}

	@Deprecated
	public AddToOperation(Entity entity, Mention mention) {
		this.entity = entity;
		this.mention = mention;
	}

	@Override
	public String toString() {
		return "addTo(" + entity.getLabel() + "," + mention.getBegin() + "," + mention.getEnd() + ")";
	}

	public Entity getEntity() {
		return entity;
	}

	public Mention getMention() {
		return mention;
	}

	@Override
	public void revert(CoreferenceModel model) {
		model.remove(mention);
	}

	@Override
	void perform(CoreferenceModel model) {
		model.addTo(entity, span.begin, span.end);
	}

}
