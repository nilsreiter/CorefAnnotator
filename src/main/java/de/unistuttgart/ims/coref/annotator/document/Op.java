package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.cas.FeatureStructure;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Multimaps;

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

		public GroupEntities(Iterable<Entity> entities) {
			this.entities = Lists.immutable.withAll(entities);
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

		public MergeEntities(Iterable<Entity> entities) {
			this.entities = Lists.immutable.withAll(entities);
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

	public abstract class MoveOp<M extends FeatureStructure, C extends FeatureStructure> implements Op {
		ImmutableList<M> objects;
		C source;
		C target;

		public MoveOp(C source, C target, Iterable<M> mention) {
			this.objects = Lists.immutable.withAll(mention);
			this.source = source;
			this.target = target;
		}

		@SafeVarargs
		public MoveOp(C source, C target, M... mention) {
			this.objects = Lists.immutable.of(mention);
			this.source = source;
			this.target = target;
		}

		public ImmutableList<M> getObjects() {
			return objects;
		}

		public void setObjects(ImmutableList<M> objects) {
			this.objects = objects;
		}

		public C getSource() {
			return source;
		}

		public void setSource(C source) {
			this.source = source;
		}

		public C getTarget() {
			return target;
		}

		public void setTarget(C target) {
			this.target = target;
		}

		public FeatureStructureEvent toEvent() {
			return Event.get(Event.Type.Move, getSource(), getTarget(), getObjects());
		}

		public FeatureStructureEvent toReversedEvent() {
			return Event.get(Event.Type.Move, getTarget(), getSource(), getObjects());
		}
	}

	public class MoveMentionsToEntity extends MoveOp<Mention, Entity> {

		public MoveMentionsToEntity(Entity target, Iterable<Mention> mention) {
			super(mention.iterator().next().getEntity(), target, mention);
		}

		public MoveMentionsToEntity(Entity target, Mention... mention) {
			super(mention[0].getEntity(), target, mention);
		}

		public ImmutableList<Mention> getMentions() {
			return objects;
		}

	}

	public class MoveMentionPartToMention extends MoveOp<DetachedMentionPart, Mention> {
		Mention from, to;
		DetachedMentionPart part;

		public MoveMentionPartToMention(Mention target, DetachedMentionPart part) {
			super(part.getMention(), target, part);
		}

	}

	public class RemoveEntities implements Op {
		ImmutableList<Entity> entities;
		MutableSetMultimap<Entity, EntityGroup> entityEntityGroupMap = Multimaps.mutable.set.empty();

		public RemoveEntities(Entity... entities) {
			this.entities = Lists.immutable.of(entities);
		}

		public RemoveEntities(Iterable<Entity> entities) {
			this.entities = Lists.immutable.withAll(entities);
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

	public class RemoveDuplicateMentionsInEntities implements Op {
		final ImmutableList<Entity> entities;

		ImmutableSet<Mention> removedMentions;

		public RemoveDuplicateMentionsInEntities(Iterable<Entity> entities) {
			this.entities = Lists.immutable.withAll(entities);
		}

		public RemoveDuplicateMentionsInEntities(Entity... entities) {
			this.entities = Lists.immutable.of(entities);
		}

		public ImmutableSet<Mention> getRemovedMentions() {
			return removedMentions;
		}

		public void setRemovedMentions(ImmutableSet<Mention> removedMentions) {
			this.removedMentions = removedMentions;
		}

		public ImmutableList<Entity> getEntities() {
			return entities;
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

	public class ToggleEntityFlag extends ToggleFlag<Entity> {

		public ToggleEntityFlag(String flag, Iterable<Entity> objects) {
			super(flag, objects);
		}

	}

	public class ToggleMentionFlag extends ToggleFlag<Mention> {

		public ToggleMentionFlag(String flag, Iterable<Mention> objects) {
			super(flag, objects);
		}

	}

	public abstract class ToggleFlag<T extends FeatureStructure> extends UpdateOp<T> {

		String flag;

		@SafeVarargs
		public ToggleFlag(String flag, T... objects) {
			super(objects);
			this.flag = flag;
		}

		public ToggleFlag(String flag, Iterable<T> objects) {
			super(objects);
			this.flag = flag;
		}

		public String getFlag() {
			return flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
		}

	}

	public class RenameEntity extends UpdateOp<Entity> {

		String newLabel;
		String oldLabel;

		public RenameEntity(Entity entity, String newName) {
			super(entity);
			this.oldLabel = entity.getLabel();
			this.newLabel = newName;
		}

		public Entity getEntity() {
			return this.getObjects().getFirst();
		}

		public String getNewLabel() {
			return newLabel;
		}

		public String getOldLabel() {
			return oldLabel;
		}

	}

	public class UpdateEntityColor extends UpdateOp<Entity> {
		int oldColor;
		int newColor;

		public UpdateEntityColor(int newColor, Entity entity) {
			super(entity);
			this.newColor = newColor;
			this.oldColor = entity.getColor();
		}

		public int getOldColor() {
			return oldColor;
		}

		public void setOldColor(int oldColor) {
			this.oldColor = oldColor;
		}

		public int getNewColor() {
			return newColor;
		}

		public void setNewColor(int newColor) {
			this.newColor = newColor;
		}
	}

	public class UpdateEntityKey extends UpdateOp<Entity> {
		Character oldKey = null;
		Character newKey;
		Entity previousOwner;

		public UpdateEntityKey(char newKey, Entity entity) {
			super(entity);
			if (entity.getKey() != null)
				oldKey = entity.getKey().charAt(0);
			this.newKey = newKey;
		}

		public Character getOldKey() {
			return oldKey;
		}

		public void setOldKey(Character oldKey) {
			this.oldKey = oldKey;
		}

		public Character getNewKey() {
			return newKey;
		}

		public void setNewKey(Character newKey) {
			this.newKey = newKey;
		}

		public Entity getPreviousOwner() {
			return previousOwner;
		}

		public void setPreviousOwner(Entity previousOwner) {
			this.previousOwner = previousOwner;
		}

		public Entity getEntity() {
			return getObjects().getFirst();
		}
	}

	public abstract class UpdateOp<T extends FeatureStructure> implements Op {
		ImmutableList<T> objects;

		@SafeVarargs
		public UpdateOp(T... objects) {
			this.objects = Lists.immutable.of(objects);
		}

		public UpdateOp(Iterable<T> objects) {
			this.objects = Lists.immutable.withAll(objects);
		}

		public ImmutableList<T> getObjects() {
			return objects;
		}

		public void setObjects(ImmutableList<T> objects) {
			this.objects = objects;
		}

	}
}
