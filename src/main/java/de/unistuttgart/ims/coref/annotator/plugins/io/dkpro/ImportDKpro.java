package de.unistuttgart.ims.coref.annotator.plugins.io.dkpro;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain;
import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class ImportDKpro extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (CoreferenceChain cc : JCasUtil.select(jcas, CoreferenceChain.class)) {
			Entity e = new Entity(jcas);
			e.addToIndexes();
			CoreferenceLink link = cc.getFirst();
			while (link != null) {
				Mention m = AnnotationFactory.createAnnotation(jcas, link.getBegin(), link.getEnd(), Mention.class);
				m.setEntity(e);
			}
		}

	}

}
