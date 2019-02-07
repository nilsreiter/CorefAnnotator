package de.unistuttgart.ims.coref.annotator.document.op;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;

public class UpdateEntityKey extends UpdateOp<Entity> implements CoreferenceModelOperation {
	Character oldKey = null;
	Character newKey;
	Entity previousOwner;

	public UpdateEntityKey(char newKey, Entity entity) {
		super(entity);
		if (entity.getKey() != null)
			oldKey = entity.getKey().charAt(0);
		this.newKey = newKey;
	}

	public UpdateEntityKey(Entity entity) {
		super(entity);
		if (entity.getKey() != null)
			oldKey = entity.getKey().charAt(0);
		this.newKey = null;
	}

	public Character getOldKey() {
		return oldKey;
	}

	public void setOldKey(Character oldKey) {
		this.oldKey = oldKey;
	}

	public Character getNewKey() {
		return newKey;
	}

	public void setNewKey(Character newKey) {
		this.newKey = newKey;
	}

	public Entity getPreviousOwner() {
		return previousOwner;
	}

	public void setPreviousOwner(Entity previousOwner) {
		this.previousOwner = previousOwner;
	}

	public Entity getEntity() {
		return getObjects().getFirst();
	}
}