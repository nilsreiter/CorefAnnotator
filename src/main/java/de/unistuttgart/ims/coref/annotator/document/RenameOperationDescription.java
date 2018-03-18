package de.unistuttgart.ims.coref.annotator.document;

import de.unistuttgart.ims.coref.annotator.api.Entity;

public class RenameOperationDescription implements Op {

	Entity entity;
	String newLabel;
	String oldLabel;

	public RenameOperationDescription(Entity entity, String newName) {
		this.entity = entity;
		this.oldLabel = entity.getLabel();
		this.newLabel = newName;
	}

	public String getNewLabel() {
		return newLabel;
	}

	public String getOldLabel() {
		return oldLabel;
	}

	public Entity getEntity() {
		return entity;
	}

}
