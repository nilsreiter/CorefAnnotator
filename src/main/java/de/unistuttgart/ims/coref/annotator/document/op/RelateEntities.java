package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityRelationType;

public class RelateEntities implements RelationModelOperation {
	EntityRelationType relationType;
	ImmutableList<Entity> entities;

	public RelateEntities(EntityRelationType relationType, ImmutableList<Entity> entities) {
		this.relationType = relationType;
		this.entities = entities;
	}

	/**
	 * from, to
	 * 
	 * @param relationType
	 * @param entities
	 */
	public RelateEntities(EntityRelationType relationType, Entity... entities) {
		this.relationType = relationType;
		this.entities = Lists.immutable.with(entities);
	}
}
