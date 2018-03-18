package de.unistuttgart.ims.coref.annotator.document;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.Entity;

public class BatchAddOperationDescription implements EditOperationDescription {
	ImmutableList<Span> spans;
	Entity entity;

	public BatchAddOperationDescription(Span... span) {
		this.spans = Lists.immutable.of(span);
	}

	public BatchAddOperationDescription(Iterable<Span> span) {
		this.spans = Lists.immutable.withAll(span);
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public ImmutableList<Span> getSpans() {
		return spans;
	}

}
