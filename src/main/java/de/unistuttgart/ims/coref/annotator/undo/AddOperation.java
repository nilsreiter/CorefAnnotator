package de.unistuttgart.ims.coref.annotator.undo;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;

public class AddOperation extends AbstractEditOperation {
	Span span;
	@Deprecated
	Entity entity;
	Mention mention;

	public AddOperation(Span span) {
		this.span = span;
	}

	@Deprecated
	public AddOperation(Entity entity, Mention mention) {
		this.entity = entity;
		this.mention = mention;
	}

	@Override
	public String toString() {
		return "add(" + mention.getBegin() + "," + mention.getEnd() + ")";
	}

	@Deprecated
	public Entity getEntity() {
		return entity;
	}

	public Mention getMention() {
		return mention;
	}

	@Override
	public void revert(CoreferenceModel model) {
		model.remove(mention);
		model.remove(mention.getEntity());
	}

	@Override
	public void perform(CoreferenceModel model) {
		mention = model.add(mention.getBegin(), mention.getEnd());
	}
}
