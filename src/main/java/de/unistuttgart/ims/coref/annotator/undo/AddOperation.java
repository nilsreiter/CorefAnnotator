package de.unistuttgart.ims.coref.annotator.undo;

import de.unistuttgart.ims.coref.annotator.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class AddOperation implements EditOperation {
	Entity entity;
	Mention mention;

	public AddOperation(Entity entity, Mention mention) {
		this.entity = entity;
		this.mention = mention;
	}

	@Override
	public String toString() {
		return "add(" + mention.getBegin() + "," + mention.getEnd() + ")";
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
		model.remove(entity);
	}
}
