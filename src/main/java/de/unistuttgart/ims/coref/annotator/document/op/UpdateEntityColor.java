package de.unistuttgart.ims.coref.annotator.document.op;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;

public class UpdateEntityColor extends UpdateOp<Entity> {
	int oldColor;
	int newColor;

	public UpdateEntityColor(int newColor, Entity entity) {
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