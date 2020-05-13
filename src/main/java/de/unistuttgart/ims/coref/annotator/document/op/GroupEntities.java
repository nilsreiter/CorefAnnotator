package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v2.Entity;

public class GroupEntities implements CoreferenceModelOperation {
	ImmutableList<Entity> entities;
	Entity entityGroup;

	public GroupEntities(Entity... entities) {
		this.entities = Lists.immutable.of(entities);
	}

	public GroupEntities(Iterable<Entity> entities) {
		this.entities = Lists.immutable.withAll(entities);
	}

	public ImmutableList<Entity> getEntities() {
		return entities;
	}

	public Entity getEntityGroup() {
		return entityGroup;
	}

	public void setEntities(ImmutableList<Entity> entities) {
		this.entities = entities;
	}

	public void setEntityGroup(Entity entityGroup) {
		this.entityGroup = entityGroup;
	}

}