package de.unistuttgart.ims.coref.annotator.document.op;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;

public class RenameEntity extends UpdateOp<Entity> {

	String newLabel;
	String oldLabel;

	public RenameEntity(Entity entity, String newName) {
		super(entity);
		this.oldLabel = entity.getLabel();
		this.newLabel = newName;
	}

	public Entity getEntity() {
		return this.getObjects().getFirst();
	}

	public String getNewLabel() {
		return newLabel;
	}

	public String getOldLabel() {
		return oldLabel;
	}

}