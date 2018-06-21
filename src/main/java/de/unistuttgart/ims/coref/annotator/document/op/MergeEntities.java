package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class MergeEntities implements CoreferenceModelOperation {
	ImmutableList<Entity> entities;
	Entity entity;
	ImmutableSetMultimap<Entity, Mention> previousState;

	public MergeEntities(Entity... entities) {
		this.entities = Lists.immutable.of(entities);
	}

	public MergeEntities(Iterable<Entity> entities) {
		this.entities = Lists.immutable.withAll(entities);
	}

	public ImmutableSetMultimap<Entity, Mention> getPreviousState() {
		return previousState;
	}

	public void setPreviousState(ImmutableSetMultimap<Entity, Mention> previousState) {
		this.previousState = previousState;
	}

	public ImmutableList<Entity> getEntities() {
		return entities;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}
}