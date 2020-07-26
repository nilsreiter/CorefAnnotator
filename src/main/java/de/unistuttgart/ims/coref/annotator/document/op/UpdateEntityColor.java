package de.unistuttgart.ims.coref.annotator.document.op;

import  de.unistuttgart.ims.coref.annotator.api.v2.Entity;

public class UpdateEntityColor extends UpdateOperation<Entity> implements CoreferenceModelOperation {
	int oldColor;
	int newColor;

	public UpdateEntityColor(Entity entity, int newColor) {
		super(entity);
		this.newColor = newColor;
		this.oldColor = entity.getColor();
	}

	public int getOldColor() {
		return oldColor;
	}

	public void setOldColor(int oldColor) {
		this.oldColor = oldColor;
	}

	public int getNewColor() {
		return newColor;
	}

	public void setNewColor(int newColor) {
		this.newColor = newColor;
	}
}