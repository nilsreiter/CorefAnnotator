package de.unistuttgart.ims.coref.annotator;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;

@Deprecated
public class EntityTreeNode extends CATreeNode {

	private static final long serialVersionUID = 1L;

	Character keyCode = null;

	boolean visible = true;

	public EntityTreeNode(Entity featureStructure, String label) {
		super(featureStructure, label);
	}

	public EntityTreeNode(Entity featureStructure) {
		super(featureStructure, featureStructure.getLabel());
	}

	public Character getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(char keyCode) {
		this.keyCode = keyCode;
	}

	@Override
	public CATreeNode getChildAt(int i) {
		return (CATreeNode) super.getChildAt(i);
	}

	@Override
	public Entity getFeatureStructure() {
		return (Entity) super.getFeatureStructure();
	}

	@Override
	public String getLabel() {
		return getFeatureStructure().getLabel();
	}

	@Override
	public boolean isVirtual() {
		return !(getFeatureStructure() instanceof EntityGroup);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
