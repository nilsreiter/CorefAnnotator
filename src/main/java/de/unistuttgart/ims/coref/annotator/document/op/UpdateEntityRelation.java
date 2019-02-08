package de.unistuttgart.ims.coref.annotator.document.op;

import de.unistuttgart.ims.coref.annotator.api.v1.DirectedEntityRelation;

public class UpdateEntityRelation extends UpdateOperation<DirectedEntityRelation> implements RelationModelOperation {

	public enum EntityRelationProperty {
		TYPE, SOURCE, TARGET;
	};

	Object oldValue, newValue;
	EntityRelationProperty entityRelationProperty;

	public UpdateEntityRelation(DirectedEntityRelation relation, EntityRelationProperty property, Object newValue) {
		super(relation);
		this.entityRelationProperty = property;
		this.newValue = newValue;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public void setOldValue(Object oldValue) {
		this.oldValue = oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}

	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}

	public EntityRelationProperty getEntityRelationProperty() {
		return entityRelationProperty;
	}

	public void setEntityRelationProperty(EntityRelationProperty entityRelationProperty) {
		this.entityRelationProperty = entityRelationProperty;
	}
}
