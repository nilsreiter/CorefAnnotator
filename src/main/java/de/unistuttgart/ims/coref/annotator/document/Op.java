package de.unistuttgart.ims.coref.annotator.document;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public interface Op {

	public class RemoveEntities implements Op {
		ImmutableList<Entity> entities;

		public RemoveEntities(Entity... entities) {
			this.entities = Lists.immutable.of(entities);
		}

		public ImmutableList<Entity> getEntities() {
			return entities;
		}

		public void setEntities(ImmutableList<Entity> entities) {
			this.entities = entities;
		}

	}

	public class RemoveMention implements Op {
		ImmutableList<Mention> mentions;
		Entity entity;
		ImmutableList<Span> spans;

		public RemoveMention(Mention... mention) {
			this.mentions = Lists.immutable.of(mention);
			this.spans = mentions.collect(m -> new Span(m));
			this.entity = mentions.getFirst().getEntity();
		}

		public Mention getMention() {
			return mentions.getFirst();
		}

		public void setMentions(ImmutableList<Mention> mention) {
			this.mentions = mention;
		}

		public Span getSpan() {
			return spans.getFirst();
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

	public class RenameEntity implements Op {

		Entity entity;
		String newLabel;
		String oldLabel;

		public RenameEntity(Entity entity, String newName) {
			this.entity = entity;
			this.oldLabel = entity.getLabel();
			this.newLabel = newName;
		}

		public String getNewLabel() {
			return newLabel;
		}

		public String getOldLabel() {
			return oldLabel;
		}

		public Entity getEntity() {
			return entity;
		}

	}

	public class AddMentionsToEntity implements Op {
		Entity entity;
		ImmutableList<Span> spans;
		ImmutableList<Mention> mentions = null;

		public AddMentionsToEntity(Entity entity, Span... spans) {
			this.spans = Lists.immutable.of(spans);
			this.entity = entity;
		}

		public AddMentionsToEntity(Entity entity, Iterable<Span> spans) {
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

		@Override
		public String toString() {
			return getClass().getSimpleName() + "(" + spans.makeString(",") + ")";
		}

	}
}
