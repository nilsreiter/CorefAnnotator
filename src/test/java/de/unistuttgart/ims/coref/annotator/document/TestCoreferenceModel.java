package de.unistuttgart.ims.coref.annotator.document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.prefs.Preferences;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.multimap.set.MutableSetMultimap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.document.Event.Type;

public class TestCoreferenceModel {

	CoreferenceModel model;
	JCas jcas;
	static Preferences preferences;
	DummyListener listener;

	@BeforeClass
	public static void setUpClass() {
		preferences = Preferences.systemRoot();
		preferences.putBoolean(Constants.CFG_FULL_TOKENS, false);
	}

	@Before
	public void setUp() throws UIMAException {
		jcas = JCasFactory.createText("the dog barks.");
		model = new CoreferenceModel(new DocumentModel(jcas), preferences);
		listener = new DummyListener();
		model.addCoreferenceModelListener(listener);
	}

	@Test
	public void testEditAddMentionsToNewEntity() {
		model.edit(new Op.AddMentionsToNewEntity(new Span(0, 2)));
		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertEquals(1, JCasUtil.select(jcas, Mention.class).size());
		assertFalse(model.getMentions(0).isEmpty());
		assertFalse(model.getMentions(1).isEmpty());
		assertTrue(model.getMentions(2).isEmpty());
		assertTrue(model.getMentions(3).isEmpty());

		Mention m = JCasUtil.selectByIndex(jcas, Mention.class, 0);
		assertNotNull(m.getEntity());
		assertNotNull(m.getEntity().getLabel());
		assertNotNull(m.getEntity().getColor());
		assertNotNull(m.getEntity().getFlags());

		assertEquals(2, listener.events.size());
		assertEquals(Lists.immutable.of(Type.Add, Type.Add), listener.events.collect(ev -> ev.eventType));

		model.undo();

		assertFalse(JCasUtil.exists(jcas, Mention.class));
		assertTrue(model.getMentions(0).isEmpty());
		assertTrue(model.getMentions(1).isEmpty());
		assertTrue(model.getMentions(2).isEmpty());
		assertTrue(model.getMentions(3).isEmpty());

		assertEquals(Lists.immutable.of(Type.Add, Type.Add, Type.Remove, Type.Remove),
				listener.events.collect(ev -> ev.eventType));

	}

	@Test
	public void testEditAddMentionsToExistingEntity() {
		Entity e = new Entity(jcas);
		e.setLabel("Test");
		e.setColor(0);
		e.setFlags(new StringArray(jcas, 0));
		e.addToIndexes();

		model.edit(new Op.AddMentionsToEntity(e, new Span(1, 3)));

		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertEquals(1, JCasUtil.select(jcas, Mention.class).size());
		assertTrue(model.getMentions(0).isEmpty());
		assertFalse(model.getMentions(1).isEmpty());
		assertFalse(model.getMentions(2).isEmpty());
		assertTrue(model.getMentions(3).isEmpty());

		Mention m = JCasUtil.selectByIndex(jcas, Mention.class, 0);
		assertEquals(e, m.getEntity());

		assertEquals(1, listener.events.size());
		assertEquals(Lists.immutable.of(Event.Type.Add), listener.events.collect(ev -> ev.eventType));

		model.undo();

		assertFalse(JCasUtil.exists(jcas, Mention.class));
		assertEquals(0, JCasUtil.select(jcas, Mention.class).size());
		assertTrue(model.getMentions(0).isEmpty());
		assertTrue(model.getMentions(1).isEmpty());
		assertTrue(model.getMentions(2).isEmpty());
		assertTrue(model.getMentions(3).isEmpty());

		assertEquals(2, listener.events.size());
		assertEquals(Lists.immutable.of(Event.Type.Add, Event.Type.Remove),
				listener.events.collect(ev -> ev.eventType));

	}

	@Test
	public void testEditAttachPart() {
		Mention m = model.createMention(1, 3);

		model.edit(new Op.AttachPart(m, new Span(4, 5)));

		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertTrue(JCasUtil.exists(jcas, DetachedMentionPart.class));
		assertEquals(1, JCasUtil.select(jcas, Mention.class).size());
		assertEquals(1, JCasUtil.select(jcas, DetachedMentionPart.class).size());
		assertTrue(model.getMentions(0).isEmpty());
		assertFalse(model.getMentions(1).isEmpty());
		assertFalse(model.getMentions(2).isEmpty());
		assertTrue(model.getMentions(3).isEmpty());
		assertFalse(model.getMentions(4).isEmpty());
		assertTrue(model.getMentions(5).isEmpty());
		assertTrue(model.getMentions(6).isEmpty());

		assertEquals(Lists.immutable.of(Type.Add), listener.events.collect(ev -> ev.eventType));

		model.undo();

		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertFalse(JCasUtil.exists(jcas, DetachedMentionPart.class));
		assertEquals(1, JCasUtil.select(jcas, Mention.class).size());
		assertEquals(0, JCasUtil.select(jcas, DetachedMentionPart.class).size());

		assertTrue(model.getMentions(0).isEmpty());
		assertFalse(model.getMentions(1).isEmpty());
		assertFalse(model.getMentions(2).isEmpty());
		assertTrue(model.getMentions(3).isEmpty());
		assertTrue(model.getMentions(4).isEmpty());
		assertTrue(model.getMentions(5).isEmpty());
		assertTrue(model.getMentions(6).isEmpty());

		assertEquals(Lists.immutable.of(Type.Add, Type.Remove), listener.events.collect(ev -> ev.eventType));

	}

	@Test
	public void testEditRemoveMention() {
		Mention m = model.createMention(1, 3);

		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertEquals(1, JCasUtil.select(jcas, Mention.class).size());
		assertTrue(model.getMentions(0).isEmpty());
		assertFalse(model.getMentions(1).isEmpty());
		assertFalse(model.getMentions(2).isEmpty());
		assertTrue(model.getMentions(3).isEmpty());

		model.edit(new Op.RemoveMention(m));

		assertFalse(JCasUtil.exists(jcas, Mention.class));
		assertEquals(0, JCasUtil.select(jcas, Mention.class).size());
		assertTrue(model.getMentions(0).isEmpty());
		assertTrue(model.getMentions(1).isEmpty());
		assertTrue(model.getMentions(2).isEmpty());
		assertTrue(model.getMentions(3).isEmpty());

		model.undo();

		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertEquals(1, JCasUtil.select(jcas, Mention.class).size());
		assertTrue(model.getMentions(0).isEmpty());
		assertFalse(model.getMentions(1).isEmpty());
		assertFalse(model.getMentions(2).isEmpty());
		assertTrue(model.getMentions(3).isEmpty());

	}

	@Test
	public void testEditRemoveMention2() {
		Mention m = model.createMention(1, 3);
		DetachedMentionPart dmp = model.createDetachedMentionPart(4, 6);
		m.setDiscontinuous(dmp);
		dmp.setMention(m);

		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertTrue(JCasUtil.exists(jcas, DetachedMentionPart.class));
		assertEquals(1, JCasUtil.select(jcas, Mention.class).size());
		assertEquals(1, JCasUtil.select(jcas, DetachedMentionPart.class).size());
		assertTrue(model.getMentions(0).isEmpty());
		assertFalse(model.getMentions(1).isEmpty());
		assertFalse(model.getMentions(2).isEmpty());
		assertTrue(model.getMentions(3).isEmpty());
		assertFalse(model.getMentions(4).isEmpty());
		assertFalse(model.getMentions(5).isEmpty());
		assertTrue(model.getMentions(6).isEmpty());

		model.edit(new Op.RemoveMention(m));

		assertEquals(Lists.immutable.of(Type.Remove, Type.Remove), listener.events.collect(ev -> ev.eventType));

		assertFalse(JCasUtil.exists(jcas, Mention.class));
		assertFalse(JCasUtil.exists(jcas, DetachedMentionPart.class));
		assertEquals(0, JCasUtil.select(jcas, Mention.class).size());
		assertEquals(0, JCasUtil.select(jcas, DetachedMentionPart.class).size());
		assertTrue(model.getMentions(0).isEmpty());
		assertTrue(model.getMentions(1).isEmpty());
		assertTrue(model.getMentions(2).isEmpty());
		assertTrue(model.getMentions(3).isEmpty());
		assertTrue(model.getMentions(4).isEmpty());
		assertTrue(model.getMentions(5).isEmpty());
		assertTrue(model.getMentions(6).isEmpty());

		model.undo();

		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertTrue(JCasUtil.exists(jcas, DetachedMentionPart.class));
		assertEquals(1, JCasUtil.select(jcas, Mention.class).size());
		assertEquals(1, JCasUtil.select(jcas, DetachedMentionPart.class).size());
		assertTrue(model.getMentions(0).isEmpty());
		assertFalse(model.getMentions(1).isEmpty());
		assertFalse(model.getMentions(2).isEmpty());
		assertTrue(model.getMentions(3).isEmpty());
		assertFalse(model.getMentions(4).isEmpty());
		assertFalse(model.getMentions(5).isEmpty());
		assertTrue(model.getMentions(6).isEmpty());
	}

	@Test
	public void testEditMergeEntities() {
		model.edit(new Op.AddMentionsToNewEntity(new Span(0, 1), new Span(2, 3)));
		model.edit(new Op.AddMentionsToNewEntity(new Span(4, 5), new Span(6, 7)));

		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertTrue(JCasUtil.exists(jcas, Entity.class));
		assertEquals(2, JCasUtil.select(jcas, Entity.class).size());
		assertEquals(4, JCasUtil.select(jcas, Mention.class).size());

		listener.reset();

		model.edit(new Op.MergeEntities(JCasUtil.select(jcas, Entity.class).toArray(new Entity[2])));

		assertEquals(Lists.immutable.of(Type.Move, Type.Remove), listener.events.collect(ev -> ev.eventType));

		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertTrue(JCasUtil.exists(jcas, Entity.class));
		assertEquals(1, JCasUtil.select(jcas, Entity.class).size());
		assertEquals(4, JCasUtil.select(jcas, Mention.class).size());
		Entity e = JCasUtil.selectSingle(jcas, Entity.class);
		for (Mention m : JCasUtil.select(jcas, Mention.class))
			assertEquals(e, m.getEntity());

		listener.reset();

		model.undo();

		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertTrue(JCasUtil.exists(jcas, Entity.class));
		assertEquals(2, JCasUtil.select(jcas, Entity.class).size());
		assertEquals(4, JCasUtil.select(jcas, Mention.class).size());

		MutableSetMultimap<Entity, Mention> map = Sets.mutable.withAll(JCasUtil.select(jcas, Mention.class))
				.groupBy(m -> m.getEntity());
		assertEquals(2, map.keySet().size());

		assertEquals(Lists.immutable.of(Type.Add, Type.Move), listener.events.collect(ev -> ev.eventType));

	}

	@Test
	public void testEditMoveEntities() {
		model.edit(new Op.AddMentionsToNewEntity(new Span(0, 1), new Span(2, 3)));
		model.edit(new Op.AddMentionsToNewEntity(new Span(4, 5), new Span(6, 7)));
		MutableList<Entity> entities = Lists.mutable.withAll(JCasUtil.select(jcas, Entity.class));

		Mention m = ((Mention) model.getMentions(0).iterator().next());
		assertNotEquals(entities.get(1), m.getEntity());
		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertTrue(JCasUtil.exists(jcas, Entity.class));
		assertEquals(2, JCasUtil.select(jcas, Entity.class).size());
		assertEquals(4, JCasUtil.select(jcas, Mention.class).size());
		assertEquals(2, model.entityMentionMap.get(entities.get(1)).size());
		assertEquals(2, model.entityMentionMap.get(entities.get(0)).size());

		model.edit(new Op.MoveMentionsToEntity(entities.get(1), m));

		assertEquals(entities.get(1), m.getEntity());
		assertEquals(2, JCasUtil.select(jcas, Entity.class).size());
		assertEquals(4, JCasUtil.select(jcas, Mention.class).size());
		assertEquals(3, model.entityMentionMap.get(entities.get(1)).size());
		assertEquals(1, model.entityMentionMap.get(entities.get(0)).size());

		model.undo();

		assertNotEquals(entities.get(1), m.getEntity());
		assertEquals(2, JCasUtil.select(jcas, Entity.class).size());
		assertEquals(4, JCasUtil.select(jcas, Mention.class).size());
		assertEquals(2, model.entityMentionMap.get(entities.get(1)).size());
		assertEquals(2, model.entityMentionMap.get(entities.get(0)).size());

	}

	@Test
	public void testRemovEntityFromGroup() {
		Op.AddMentionsToNewEntity op;
		Entity e1, e2;
		op = new Op.AddMentionsToNewEntity(new Span(1, 2));
		model.edit(op);
		e1 = op.getEntity();
		op = new Op.AddMentionsToNewEntity(new Span(3, 4));
		model.edit(op);
		e2 = op.getEntity();

		assertTrue(JCasUtil.exists(jcas, Entity.class));
		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertEquals(2, JCasUtil.select(jcas, Entity.class).size());
		assertEquals(2, JCasUtil.select(jcas, Mention.class).size());
		assertFalse(JCasUtil.exists(jcas, EntityGroup.class));

		model.edit(new Op.GroupEntities(e1, e2));

		assertTrue(JCasUtil.exists(jcas, Entity.class));
		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertEquals(3, JCasUtil.select(jcas, Entity.class).size());
		assertEquals(2, JCasUtil.select(jcas, Mention.class).size());
		assertTrue(JCasUtil.exists(jcas, EntityGroup.class));

		EntityGroup eg = JCasUtil.select(jcas, EntityGroup.class).iterator().next();
		assertEquals(2, eg.getMembers().size());

		model.edit(new Op.RemoveEntitiesFromEntityGroup(eg, e1));

		assertTrue(JCasUtil.exists(jcas, Entity.class));
		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertEquals(3, JCasUtil.select(jcas, Entity.class).size());
		assertEquals(2, JCasUtil.select(jcas, Mention.class).size());
		assertTrue(JCasUtil.exists(jcas, EntityGroup.class));

		eg = JCasUtil.select(jcas, EntityGroup.class).iterator().next();
		assertEquals(1, eg.getMembers().size());

		model.undo();

		assertTrue(JCasUtil.exists(jcas, Entity.class));
		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertEquals(3, JCasUtil.select(jcas, Entity.class).size());
		assertEquals(2, JCasUtil.select(jcas, Mention.class).size());
		assertTrue(JCasUtil.exists(jcas, EntityGroup.class));
		eg = JCasUtil.select(jcas, EntityGroup.class).iterator().next();
		assertEquals(2, eg.getMembers().size());

	}

	@Test
	public void testSequence1() {
		Entity e = model.createEntity("test");
		Mention m = model.createMention(1, 3);
		m.setEntity(e);

		model.edit(new Op.AttachPart(m, new Span(4, 5)));

		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertTrue(JCasUtil.exists(jcas, DetachedMentionPart.class));
		assertEquals(1, JCasUtil.select(jcas, Mention.class).size());
		assertEquals(1, JCasUtil.select(jcas, DetachedMentionPart.class).size());
		assertTrue(model.getMentions(0).isEmpty());
		assertFalse(model.getMentions(1).isEmpty());
		assertFalse(model.getMentions(2).isEmpty());
		assertTrue(model.getMentions(3).isEmpty());
		assertFalse(model.getMentions(4).isEmpty());
		assertTrue(model.getMentions(5).isEmpty());
		assertTrue(model.getMentions(6).isEmpty());

		model.edit(new Op.RemoveMention(m));

		assertFalse(JCasUtil.exists(jcas, Mention.class));
		assertFalse(JCasUtil.exists(jcas, DetachedMentionPart.class));
		assertEquals(0, JCasUtil.select(jcas, Mention.class).size());
		assertEquals(0, JCasUtil.select(jcas, DetachedMentionPart.class).size());
		assertTrue(model.getMentions(0).isEmpty());
		assertTrue(model.getMentions(1).isEmpty());
		assertTrue(model.getMentions(2).isEmpty());
		assertTrue(model.getMentions(3).isEmpty());
		assertTrue(model.getMentions(4).isEmpty());
		assertTrue(model.getMentions(5).isEmpty());
		assertTrue(model.getMentions(6).isEmpty());

		model.undo();

		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertTrue(JCasUtil.exists(jcas, DetachedMentionPart.class));
		assertEquals(1, JCasUtil.select(jcas, Mention.class).size());
		assertEquals(1, JCasUtil.select(jcas, DetachedMentionPart.class).size());
		assertTrue(model.getMentions(0).isEmpty());
		assertFalse(model.getMentions(1).isEmpty());
		assertFalse(model.getMentions(2).isEmpty());
		assertTrue(model.getMentions(3).isEmpty());
		assertFalse(model.getMentions(4).isEmpty());
		assertTrue(model.getMentions(5).isEmpty());
		assertTrue(model.getMentions(6).isEmpty());

		model.undo();

		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertFalse(JCasUtil.exists(jcas, DetachedMentionPart.class));
		assertEquals(1, JCasUtil.select(jcas, Mention.class).size());
		assertEquals(0, JCasUtil.select(jcas, DetachedMentionPart.class).size());
		assertTrue(model.getMentions(0).isEmpty());
		assertFalse(model.getMentions(1).isEmpty());
		assertFalse(model.getMentions(2).isEmpty());
		assertTrue(model.getMentions(3).isEmpty());
		assertTrue(model.getMentions(4).isEmpty());
		assertTrue(model.getMentions(5).isEmpty());
		assertTrue(model.getMentions(6).isEmpty());

		assertEquals(0, model.getHistory().size());
	}

	@Test
	public void testRemoveDuplicates() {
		model.edit(new Op.AddMentionsToNewEntity(new Span(1, 3), new Span(1, 3), new Span(2, 4)));

		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertTrue(JCasUtil.exists(jcas, Entity.class));
		assertEquals(3, JCasUtil.select(jcas, Mention.class).size());
		assertEquals(1, JCasUtil.select(jcas, Entity.class).size());

		Entity e = JCasUtil.selectSingle(jcas, Entity.class);

		model.edit(new Op.RemoveDuplicateMentionsInEntities(e));

		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertTrue(JCasUtil.exists(jcas, Entity.class));
		assertEquals(2, JCasUtil.select(jcas, Mention.class).size());
		assertEquals(1, JCasUtil.select(jcas, Entity.class).size());

		model.undo();

		assertTrue(JCasUtil.exists(jcas, Mention.class));
		assertTrue(JCasUtil.exists(jcas, Entity.class));
		assertEquals(3, JCasUtil.select(jcas, Mention.class).size());
		assertEquals(1, JCasUtil.select(jcas, Entity.class).size());
	}

	@Test
	public void testRemoveEntityThatIsInGroup() {
		model.edit(new Op.AddMentionsToNewEntity(new Span(0, 1)));
		model.edit(new Op.AddMentionsToNewEntity(new Span(1, 2)));

		ImmutableList<Entity> entities = Lists.immutable.withAll(JCasUtil.select(jcas, Entity.class));
		ImmutableList<Mention> mentions = Lists.immutable.withAll(JCasUtil.select(jcas, Mention.class));

		assertEquals(entities.getFirst(), mentions.getFirst().getEntity());
		assertEquals(entities.getLast(), mentions.getLast().getEntity());

		model.edit(new Op.GroupEntities(entities.get(0), entities.get(1)));

		EntityGroup group = JCasUtil.select(jcas, EntityGroup.class).iterator().next();
		assertEquals(2, group.getMembers().size());
		assertEquals(entities.get(0), group.getMembers(0));
		assertEquals(entities.get(1), group.getMembers(1));

		model.edit(new Op.RemoveMention(mentions.getFirst()));
		model.edit(new Op.RemoveEntities(entities.getFirst()));

		assertEquals(1, group.getMembers().size());
		assertEquals(entities.get(1), group.getMembers(0));

		model.undo();

		assertEquals(2, group.getMembers().size());
		assertEquals(entities.get(1), group.getMembers(0));
		assertEquals(entities.get(0), group.getMembers(1));

	}
}
