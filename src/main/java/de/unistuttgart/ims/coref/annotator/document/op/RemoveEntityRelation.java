package de.unistuttgart.ims.coref.annotator.document.op;

import de.unistuttgart.ims.coref.annotator.api.v1.EntityRelation;

public class RemoveEntityRelation extends AbstractRemoveOperation<EntityRelation> implements RelationModelOperation {
	public RemoveEntityRelation(EntityRelation... featureStructures) {
		super(featureStructures);
	}
}
