package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v2.Entity;

public class RemoveEntitiesFromEntityGroup implements CoreferenceModelOperation {
	ImmutableList<Entity> entities;
	Entity entityGroup;

	public RemoveEntitiesFromEntityGroup(Entity entityGroup, Iterable<Entity> entities) {
		this.entities = Lists.immutable.withAll(entities);
		this.entityGroup = entityGroup;
	}

	public RemoveEntitiesFromEntityGroup(Entity entityGroup, Entity... entities) {
		this.entities = Lists.immutable.of(entities);
		this.entityGroup = entityGroup;
	}

	public ImmutableList<Entity> getEntities() {
		return entities;
	}

	public void setEntities(ImmutableList<Entity> entities) {
		this.entities = entities;
	}

	public Entity getEntityGroup() {
		return entityGroup;
	}

	public void setEntityGroup(Entity entityGroup) {
		this.entityGroup = entityGroup;
	}

}