package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class MoveMentionsToEntity extends MoveOperation<Mention, Entity> {

	public MoveMentionsToEntity(Entity target, Iterable<Mention> mention) {
		super(mention.iterator().next().getEntity(), target, mention);
	}

	public MoveMentionsToEntity(Entity target, Mention... mention) {
		super(mention[0].getEntity(), target, mention);
	}

	public ImmutableList<Mention> getMentions() {
		return objects;
	}

}