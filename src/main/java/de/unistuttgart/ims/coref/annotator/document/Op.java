package de.unistuttgart.ims.coref.annotator.document;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public interface Op {

	public class AddToOperation implements Op {
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

	public class AddMentionsToNewEntity implements Op {
		ImmutableList<Span> spans;
		Entity entity;

		public AddMentionsToNewEntity(Span... span) {
			this.spans = Lists.immutable.of(span);
		}

		public AddMentionsToNewEntity(Iterable<Span> span) {
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
}
