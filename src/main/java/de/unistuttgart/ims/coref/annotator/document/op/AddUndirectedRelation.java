package de.unistuttgart.ims.coref.annotator.document.op;

import de.unistuttgart.ims.coref.annotator.api.v1.SymmetricEntityRelation;

public class AddUndirectedRelation implements RelationModelOperation, RedoableOperation {

	SymmetricEntityRelation entityRelation;

	public AddUndirectedRelation() {
	}

	public SymmetricEntityRelation getEntityRelation() {
		return entityRelation;
	}

	public void setEntityRelation(SymmetricEntityRelation entityRelation) {
		this.entityRelation = entityRelation;
	}

}
