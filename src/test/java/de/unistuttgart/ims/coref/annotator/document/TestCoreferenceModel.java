package de.unistuttgart.ims.coref.annotator.document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.prefs.Preferences;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class TestCoreferenceModel {

	CoreferenceModel model;
	JCas jcas;
	static Preferences preferences;

	@BeforeClass
	public static void setUpClass() {
		preferences = Preferences.systemRoot();
		preferences.putBoolean(Constants.CFG_FULL_TOKENS, false);
	}

	@Before
	public void setUp() throws UIMAException {
		jcas = JCasFactory.createText("the dog barks.");
		model = new CoreferenceModel(jcas, preferences);
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

		model.undo();

		assertFalse(JCasUtil.exists(jcas, Mention.class));
		assertTrue(model.getMentions(0).isEmpty());
		assertTrue(model.getMentions(1).isEmpty());
		assertTrue(model.getMentions(2).isEmpty());
		assertTrue(model.getMentions(3).isEmpty());
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

		model.undo();

		assertFalse(JCasUtil.exists(jcas, Mention.class));
		assertEquals(0, JCasUtil.select(jcas, Mention.class).size());
		assertTrue(model.getMentions(0).isEmpty());
		assertTrue(model.getMentions(1).isEmpty());
		assertTrue(model.getMentions(2).isEmpty());
		assertTrue(model.getMentions(3).isEmpty());
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
}
