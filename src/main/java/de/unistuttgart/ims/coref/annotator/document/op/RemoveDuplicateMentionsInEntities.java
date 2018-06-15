package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class RemoveDuplicateMentionsInEntities implements CoreferenceModelOperation {
	final ImmutableList<Entity> entities;

	ImmutableSet<Mention> removedMentions;

	public RemoveDuplicateMentionsInEntities(Iterable<Entity> entities) {
		this.entities = Lists.immutable.withAll(entities);
	}

	public RemoveDuplicateMentionsInEntities(Entity... entities) {
		this.entities = Lists.immutable.of(entities);
	}

	public ImmutableSet<Mention> getRemovedMentions() {
		return removedMentions;
	}

	public void setRemovedMentions(ImmutableSet<Mention> removedMentions) {
		this.removedMentions = removedMentions;
	}

	public ImmutableList<Entity> getEntities() {
		return entities;
	}

}