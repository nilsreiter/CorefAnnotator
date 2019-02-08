package de.unistuttgart.ims.coref.annotator.document.op;

import de.unistuttgart.ims.coref.annotator.api.v1.DirectedEntityRelation;

public class AddDirectedRelation implements RelationModelOperation, RedoableOperation {

	DirectedEntityRelation entityRelation;

	public AddDirectedRelation() {
	}

	public DirectedEntityRelation getEntityRelation() {
		return entityRelation;
	}

	public void setEntityRelation(DirectedEntityRelation entityRelation) {
		this.entityRelation = entityRelation;
	}

}
