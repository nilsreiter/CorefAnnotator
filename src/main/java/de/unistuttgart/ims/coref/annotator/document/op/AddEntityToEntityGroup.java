package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v2.Entity;

public class AddEntityToEntityGroup implements CoreferenceModelOperation {
	Entity entityGroup;
	ImmutableList<Entity> entities;

	public AddEntityToEntityGroup(Entity entityGroup, Iterable<Entity> entities) {
		this.entityGroup = entityGroup;
		this.entities = Lists.immutable.withAll(entities);
	}

	public Entity getEntityGroup() {
		return entityGroup;
	}

	public void setEntityGroup(Entity entityGroup) {
		this.entityGroup = entityGroup;
	}

	public ImmutableList<Entity> getEntities() {
		return entities;
	}

	public void setEntities(ImmutableList<Entity> entities) {
		this.entities = entities;
	}

}