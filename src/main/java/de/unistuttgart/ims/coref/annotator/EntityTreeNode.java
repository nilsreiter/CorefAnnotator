package de.unistuttgart.ims.coref.annotator;

import de.unistuttgart.ims.coref.annotator.api.Entity;

public class EntityTreeNode extends TreeNode<Entity> {

	private static final long serialVersionUID = 1L;

	Character keyCode = null;

	public EntityTreeNode(Entity featureStructure, String label) {
		super(featureStructure, label);
	}

	public Character getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(char keyCode) {
		this.keyCode = keyCode;
	}

	@Override
	public String getLabel() {
		return getFeatureStructure().getLabel();
	}
}
