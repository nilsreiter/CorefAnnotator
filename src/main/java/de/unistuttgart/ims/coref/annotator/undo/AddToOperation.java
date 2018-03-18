package de.unistuttgart.ims.coref.annotator.undo;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class AddToOperation implements EditOperationDescription {
	Entity entity;
	Mention mention;

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

}
