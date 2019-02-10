package de.unistuttgart.ims.coref.annotator.document.op;

import de.unistuttgart.ims.coref.annotator.api.v1.EntityRelation;

public class UpdateDirectedEntityRelation extends UpdateOperation<EntityRelation> implements RelationModelOperation {

	public enum EntityRelationProperty {
		TYPE, SOURCE, TARGET, ADD_ENTITY, REMOVE_ENTITY;
	};

	Object oldValue, newValue;
	EntityRelationProperty entityRelationProperty;

	public UpdateDirectedEntityRelation(EntityRelation relation, EntityRelationProperty property, Object newValue) {
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
