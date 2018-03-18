package de.unistuttgart.ims.coref.annotator.document;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public interface Op {

	public class RemovePart implements Op {
		Mention mention;
		Span span;
		DetachedMentionPart part;

		public RemovePart(Mention mention, DetachedMentionPart part) {
			this.mention = mention;
			this.part = part;
			this.span = new Span(part);
		}

		public Mention getMention() {
			return mention;
		}

		public void setMention(Mention mention) {
			this.mention = mention;
		}

		public Span getSpan() {
			return span;
		}

		public void setSpan(Span span) {
			this.span = span;
		}

		public DetachedMentionPart getPart() {
			return part;
		}

		public void setPart(DetachedMentionPart part) {
			this.part = part;
		}

	}

	public class AttachPart implements Op {
		Mention mention;
		Span span;
		DetachedMentionPart part;

		public AttachPart(Mention mention, Span span) {
			this.mention = mention;
			this.span = span;
		}

		public Mention getMention() {
			return mention;
		}

		public void setMention(Mention mention) {
			this.mention = mention;
		}

		public Span getSpan() {
			return span;
		}

		public void setSpan(Span span) {
			this.span = span;
		}

		public DetachedMentionPart getPart() {
			return part;
		}

		public void setPart(DetachedMentionPart part) {
			this.part = part;
		}
	}

	public class GroupEntities implements Op {
		ImmutableList<Entity> entities;
		EntityGroup entityGroup;

		public GroupEntities(Entity... entities) {
			this.entities = Lists.immutable.of(entities);
		}

		public ImmutableList<Entity> getEntities() {
			return entities;
		}

		public void setEntities(ImmutableList<Entity> entities) {
			this.entities = entities;
		}

		public EntityGroup getEntityGroup() {
			return entityGroup;
		}

		public void setEntityGroup(EntityGroup entityGroup) {
			this.entityGroup = entityGroup;
		}

	}

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

	public class MoveMentionsToEntity implements Op {
		ImmutableList<Mention> mentions;
		Entity target;
		Entity source;

		public MoveMentionsToEntity(Entity target, Mention... mention) {
			this.mentions = Lists.immutable.of(mention);
			this.source = mentions.getFirst().getEntity();
			this.target = target;
		}

		public MoveMentionsToEntity(Entity target, Iterable<Mention> mention) {
			this.mentions = Lists.immutable.withAll(mention);
			this.source = mentions.getFirst().getEntity();
			this.target = target;
		}

		public ImmutableList<Mention> getMentions() {
			return mentions;
		}

		public void setMentions(ImmutableList<Mention> mentions) {
			this.mentions = mentions;
		}

		public Entity getTarget() {
			return target;
		}

		public void setTarget(Entity target) {
			this.target = target;
		}

		public Entity getSource() {
			return source;
		}

		public void setSource(Entity source) {
			this.source = source;
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
