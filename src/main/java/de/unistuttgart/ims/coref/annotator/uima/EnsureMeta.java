package de.unistuttgart.ims.coref.annotator.uima;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.coref.annotator.api.Meta;

public class EnsureMeta extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		if (!JCasUtil.exists(aJCas, Meta.class)) {
			Meta meta = new Meta(aJCas);
			meta.addToIndexes();
		}

	}

}
