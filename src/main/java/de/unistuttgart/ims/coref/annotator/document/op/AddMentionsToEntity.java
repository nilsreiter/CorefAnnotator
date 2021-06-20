package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Span;
import  de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import  de.unistuttgart.ims.coref.annotator.api.v2.Mention;

public class AddMentionsToEntity implements CoreferenceModelOperation {
	Entity entity;
	ImmutableList<Mention> mentions = null;
	ImmutableList<Span> spans;

	public AddMentionsToEntity(Entity entity, Iterable<Span> spans) {
		this.spans = Lists.immutable.withAll(spans);
		this.entity = entity;
	}

	public AddMentionsToEntity(Entity entity, Span... spans) {
		this.spans = Lists.immutable.of(spans);
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

	public ImmutableList<Mention> getMentions() {
		return mentions;
	}

	public ImmutableList<Span> getSpans() {
		return spans;
	}

	public void setMentions(ImmutableList<Mention> mentions) {
		this.mentions = mentions;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + getEntity().hashCode() + "," + spans.makeString(",") + ")";
	}
}