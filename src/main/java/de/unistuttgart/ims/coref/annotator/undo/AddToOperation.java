package de.unistuttgart.ims.coref.annotator.undo;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class AddToOperation implements EditOperationDescription {
	Entity entity;
	ImmutableList<Span> spans;
	ImmutableList<Mention> mentions = null;

	public AddToOperation(Entity entity, Span... spans) {
		this.spans = Lists.immutable.of(spans);
		this.entity = entity;
	}

	public AddToOperation(Entity entity, Iterable<Span> spans) {
		this.spans = Lists.immutable.withAll(spans);
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

	public ImmutableList<Mention> getMentions() {
		return mentions;
	}

	public void setMentions(ImmutableList<Mention> mentions) {
		this.mentions = mentions;
	}

	public ImmutableList<Span> getSpans() {
		return spans;
	}

}
