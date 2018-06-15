package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityGroup;
import de.unistuttgart.ims.coref.annotator.document.Op;

public class GroupEntities implements Op {
	ImmutableList<Entity> entities;
	EntityGroup entityGroup;

	public GroupEntities(Entity... entities) {
		this.entities = Lists.immutable.of(entities);
	}

	public GroupEntities(Iterable<Entity> entities) {
		this.entities = Lists.immutable.withAll(entities);
	}

	public ImmutableList<Entity> getEntities() {
		return entities;
	}

	public EntityGroup getEntityGroup() {
		return entityGroup;
	}

	public void setEntities(ImmutableList<Entity> entities) {
		this.entities = entities;
	}

	public void setEntityGroup(EntityGroup entityGroup) {
		this.entityGroup = entityGroup;
	}

}