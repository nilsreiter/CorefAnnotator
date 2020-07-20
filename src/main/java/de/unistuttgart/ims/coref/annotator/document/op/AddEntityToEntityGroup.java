package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import de.unistuttgart.ims.coref.annotator.api.v2.EntityGroup;

public class AddEntityToEntityGroup implements CoreferenceModelOperation {
	EntityGroup entityGroup;
	ImmutableList<Entity> entities;

	public AddEntityToEntityGroup(EntityGroup entityGroup, Iterable<Entity> entities) {
		this.entityGroup = entityGroup;
		this.entities = Lists.immutable.withAll(entities);
	}

	public EntityGroup getEntityGroup() {
		return entityGroup;
	}

	public void setEntityGroup(EntityGroup entityGroup) {
		this.entityGroup = entityGroup;
	}

	public ImmutableList<Entity> getEntities() {
		return entities;
	}

	public void setEntities(ImmutableList<Entity> entities) {
		this.entities = entities;
	}

}