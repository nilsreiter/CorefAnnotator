package de.unistuttgart.ims.coref.annotator.document.op;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class RemoveMention implements CoreferenceModelOperation {
	Entity entity;
	ImmutableList<Mention> mentions;
	ImmutableList<Span> spans;

	public RemoveMention(Mention... mention) {
		this.mentions = Lists.immutable.of(mention);
		this.spans = mentions.collect(m -> new Span(m));
		this.entity = mentions.getFirst().getEntity();
	}

	public RemoveMention(Iterable<Mention> mention) {
		this.mentions = Lists.immutable.withAll(mention);
		this.spans = mentions.collect(m -> new Span(m));
		this.entity = mentions.getFirst().getEntity();
	}

	public Entity getEntity() {
		return entity;
	}

	public Mention getMention() {
		return mentions.getFirst();
	}

	public Span getSpan() {
		return spans.getFirst();
	}

	public ImmutableList<Span> getSpans() {
		return spans;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public void setMentions(ImmutableList<Mention> mention) {
		this.mentions = mention;
	}

	public ImmutableList<Mention> getMentions() {
		return mentions;
	}
}