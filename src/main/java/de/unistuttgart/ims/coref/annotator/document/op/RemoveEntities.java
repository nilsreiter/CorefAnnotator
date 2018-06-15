package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Multimaps;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityGroup;

public class RemoveEntities implements CoreferenceModelOperation {
	ImmutableList<Entity> entities;
	public MutableSetMultimap<Entity, EntityGroup> entityEntityGroupMap = Multimaps.mutable.set.empty();

	public RemoveEntities(Entity... entities) {
		this.entities = Lists.immutable.of(entities);
	}

	public RemoveEntities(Iterable<Entity> entities) {
		this.entities = Lists.immutable.withAll(entities);
	}

	public ImmutableList<Entity> getEntities() {
		return entities;
	}

	public void setEntities(ImmutableList<Entity> entities) {
		this.entities = entities;
	}

}