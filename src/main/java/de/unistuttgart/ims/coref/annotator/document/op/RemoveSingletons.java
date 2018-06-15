package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.document.Op;

public class RemoveSingletons implements Op {
	ImmutableList<Mention> mentions;
	ImmutableList<Entity> entities;

	public ImmutableList<Mention> getMentions() {
		return mentions;
	}

	public void setMentions(ImmutableList<Mention> mentions) {
		this.mentions = mentions;
	}

	public ImmutableList<Entity> getEntities() {
		return entities;
	}

	public void setEntities(ImmutableList<Entity> entities) {
		this.entities = entities;
	}

}