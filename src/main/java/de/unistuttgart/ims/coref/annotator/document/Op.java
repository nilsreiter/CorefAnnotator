package de.unistuttgart.ims.coref.annotator.document;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public interface Op {

	public class AddEntityToEntityGroup implements Op {
		EntityGroup entityGroup;
		ImmutableList<Entity> entities;

		public AddEntityToEntityGroup(EntityGroup entityGroup, Iterable<Entity> entities) {
			this.entityGroup = entityGroup;
			this.entities = Lists.immutable.withAll(entities);
		}

		public EntityGroup getEntityGroup() {
			return entityGroup;
		}

		public void setEntityGroup(EntityGroup entityGroup) {
			this.entityGroup = entityGroup;
		}

		public ImmutableList<Entity> getEntities() {
			return entities;
		}

		public void setEntities(ImmutableList<Entity> entities) {
			this.entities = entities;
		}

	}

	public class AddMentionsToEntity implements Op {
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

	}

	public class AddMentionsToNewEntity implements Op {
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

	public class AttachPart implements Op {
		Mention mention;
		DetachedMentionPart part;
		Span span;

		public AttachPart(Mention mention, Span span) {
			this.mention = mention;
			this.span = span;
		}

		public Mention getMention() {
			return mention;
		}

		public DetachedMentionPart getPart() {
			return part;
		}

		public Span getSpan() {
			return span;
		}

		public void setMention(Mention mention) {
			this.mention = mention;
		}

		public void setPart(DetachedMentionPart part) {
			this.part = part;
		}

		public void setSpan(Span span) {
			this.span = span;
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

		public EntityGroup getEntityGroup() {
			return entityGroup;
		}

		public void setEntities(ImmutableList<Entity> entities) {
			this.entities = entities;
		}

		public void setEntityGroup(EntityGroup entityGroup) {
			this.entityGroup = entityGroup;
		}

	}

	public class MergeEntities implements Op {
		ImmutableList<Entity> entities;
		Entity entity;
		ImmutableSetMultimap<Entity, Mention> previousState;

		public MergeEntities(Entity... entities) {
			this.entities = Lists.immutable.of(entities);
		}

		public ImmutableSetMultimap<Entity, Mention> getPreviousState() {
			return previousState;
		}

		public void setPreviousState(ImmutableSetMultimap<Entity, Mention> previousState) {
			this.previousState = previousState;
		}

		public ImmutableList<Entity> getEntities() {
			return entities;
		}

		public Entity getEntity() {
			return entity;
		}

		public void setEntity(Entity entity) {
			this.entity = entity;
		}
	}

	public class MoveMentionsToEntity implements Op {
		ImmutableList<Mention> mentions;
		Entity source;
		Entity target;

		public MoveMentionsToEntity(Entity target, Iterable<Mention> mention) {
			this.mentions = Lists.immutable.withAll(mention);
			this.source = mentions.getFirst().getEntity();
			this.target = target;
		}

		public MoveMentionsToEntity(Entity target, Mention... mention) {
			this.mentions = Lists.immutable.of(mention);
			this.source = mentions.getFirst().getEntity();
			this.target = target;
		}

		public ImmutableList<Mention> getMentions() {
			return mentions;
		}

		public Entity getSource() {
			return source;
		}

		public Entity getTarget() {
			return target;
		}

		public void setMentions(ImmutableList<Mention> mentions) {
			this.mentions = mentions;
		}

		public void setSource(Entity source) {
			this.source = source;
		}

		public void setTarget(Entity target) {
			this.target = target;
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

	public class RemoveEntitiesFromEntityGroup implements Op {
		ImmutableList<Entity> entities;
		EntityGroup entityGroup;

		public RemoveEntitiesFromEntityGroup(EntityGroup entityGroup, Iterable<Entity> entities) {
			this.entities = Lists.immutable.withAll(entities);
			this.entityGroup = entityGroup;
		}

		public RemoveEntitiesFromEntityGroup(EntityGroup entityGroup, Entity... entities) {
			this.entities = Lists.immutable.of(entities);
			this.entityGroup = entityGroup;
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

	public class RemoveMention implements Op {
		Entity entity;
		ImmutableList<Mention> mentions;
		ImmutableList<Span> spans;

		public RemoveMention(Mention... mention) {
			this.mentions = Lists.immutable.of(mention);
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
	}

	public class RemovePart implements Op {
		Mention mention;
		DetachedMentionPart part;
		Span span;

		public RemovePart(Mention mention, DetachedMentionPart part) {
			this.mention = mention;
			this.part = part;
			this.span = new Span(part);
		}

		public Mention getMention() {
			return mention;
		}

		public DetachedMentionPart getPart() {
			return part;
		}

		public Span getSpan() {
			return span;
		}

		public void setMention(Mention mention) {
			this.mention = mention;
		}

		public void setPart(DetachedMentionPart part) {
			this.part = part;
		}

		public void setSpan(Span span) {
			this.span = span;
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

		public Entity getEntity() {
			return entity;
		}

		public String getNewLabel() {
			return newLabel;
		}

		public String getOldLabel() {
			return oldLabel;
		}

	}
}
