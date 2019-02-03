package de.unistuttgart.ims.coref.annotator.document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.prefs.Preferences;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.document.op.AddFlag;

public class TestFlagModel {
	DocumentModel model;
	CoreferenceModel cmodel;
	FlagModel fmodel;
	JCas jcas;
	static Preferences preferences;
	FlagModelListener listener;

	@BeforeClass
	public static void setUpClass() {
		preferences = Preferences.systemRoot();
		preferences.putBoolean(Constants.CFG_FULL_TOKENS, false);
	}

	@Before
	public void setUp() throws UIMAException {
		listener = mock(FlagModelListener.class);
		jcas = JCasFactory.createText("the dog barks.");
		model = new DocumentModel(jcas);
		fmodel = new FlagModel(model, preferences);
		fmodel.addFlagModelListener(listener);
		cmodel = new CoreferenceModel(model, preferences);
		model.setCoreferenceModel(cmodel);
		model.setFlagModel(fmodel);

	}

	@Test
	public void testInitialFlagModel() throws ClassNotFoundException {
		assertEquals(5, fmodel.getFlags().size());
		Flag flag = fmodel.getFlag(Constants.MENTION_FLAG_AMBIGUOUS);
		assertEquals(Mention.class, fmodel.getTargetClass(flag));
		assertEquals(Constants.Strings.MENTION_FLAG_AMBIGUOUS, fmodel.getLabel(flag));
	}

	@Test
	public void testAddFlag() throws ClassNotFoundException {
		AddFlag op = mock(AddFlag.class);
		doReturn(Entity.class).when(op).getTargetClass();

		fmodel.edit(op);
		assertEquals(6, fmodel.getFlags().size());
		Flag flag = fmodel.getFlags().getLast();
		assertNotNull(flag);
		assertEquals(Entity.class, fmodel.getTargetClass(flag));
		assertEquals("New Flag", flag.getLabel());

		doReturn(Mention.class).when(op).getTargetClass();
		fmodel.edit(op);
		assertEquals(7, fmodel.getFlags().size());
		flag = fmodel.getFlags().getLast();
		assertNotNull(flag);
		assertEquals(Mention.class, fmodel.getTargetClass(flag));
		assertEquals("New Flag", flag.getLabel());

		model.undo();

		assertEquals(6, fmodel.getFlags().size());
		flag = fmodel.getFlags().getLast();
		assertNotNull(flag);
		assertEquals(Entity.class, fmodel.getTargetClass(flag));
		assertEquals("New Flag", flag.getLabel());
	}

	@Test
	public void testDeleteFlag() {

	}

}
