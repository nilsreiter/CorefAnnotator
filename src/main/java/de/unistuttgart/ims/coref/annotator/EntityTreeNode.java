package de.unistuttgart.ims.coref.annotator;

import de.unistuttgart.ims.coref.annotator.api.Entity;

public class EntityTreeNode extends TreeNode<Entity> {

	private static final long serialVersionUID = 1L;

	char keyCode;

	public EntityTreeNode(Entity featureStructure, String label) {
		super(featureStructure, label);
	}

	public char getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(char keyCode) {
		this.keyCode = keyCode;
	}

}
