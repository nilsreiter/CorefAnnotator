package de.unistuttgart.ims.coref.annotator.plugin.versions.legacy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.junit.Test;
import org.xml.sax.SAXException;

public class TestLEGACY_To_V1 {

	@Test
	public void testConversion() throws UIMAException, SAXException, IOException {
		JCas jcas = JCasFactory.createJCas();
		XmiCasDeserializer.deserialize(getClass().getResourceAsStream("/Zwischen_Neun_und_Neun.txt_LEGACY.xmi"),
				jcas.getCas());

		assertNotNull(jcas);

		ImmutableList<de.unistuttgart.ims.coref.annotator.api.Entity> eList = Lists.immutable
				.withAll(JCasUtil.select(jcas, de.unistuttgart.ims.coref.annotator.api.Entity.class));
		ImmutableList<String> oldLabels = eList.collect(e -> e.getLabel());

		assertEquals(1084, eList.size());
		assertEquals(8916, JCasUtil.select(jcas, de.unistuttgart.ims.coref.annotator.api.Mention.class).size());

		SimplePipeline.runPipeline(jcas, AnalysisEngineFactory.createEngine(LEGACY_To_V1_0.class));

		ImmutableList<de.unistuttgart.ims.coref.annotator.api.v1.Entity> newList = Lists.immutable
				.withAll(JCasUtil.select(jcas, de.unistuttgart.ims.coref.annotator.api.v1.Entity.class));
		MutableList<String> newLabels = Lists.mutable.withAll(newList.collect(e -> e.getLabel()));

		newLabels.removeAll(oldLabels.castToCollection());
		System.err.println(newLabels);
		assertEquals(1086, JCasUtil.select(jcas, de.unistuttgart.ims.coref.annotator.api.v1.Entity.class).size());
		assertEquals(8916, JCasUtil.select(jcas, de.unistuttgart.ims.coref.annotator.api.v1.Mention.class).size());

	}
}
