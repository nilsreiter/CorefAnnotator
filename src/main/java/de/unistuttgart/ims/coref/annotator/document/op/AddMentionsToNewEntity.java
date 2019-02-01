package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;

public class AddMentionsToNewEntity implements CoreferenceModelOperation {
	Entity entity;
	ImmutableList<Span> spans;

	public AddMentionsToNewEntity(Iterable<Span> span) {
		this.spans = Lists.immutable.withAll(span);
	}

	public AddMentionsToNewEntity(Span... span) {
		this.spans = Lists.immutable.of(span);
	}

	public Entity getEntity() {
		return entity;
	}

	public ImmutableList<Span> getSpans() {
		return spans;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + spans.makeString(",") + ")";
	}

}