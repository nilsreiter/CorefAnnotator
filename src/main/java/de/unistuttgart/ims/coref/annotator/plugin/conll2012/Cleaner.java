package de.unistuttgart.ims.coref.annotator.plugin.conll2012;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain;
import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink;

public class Cleaner extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		CoreferenceChain ch = new CoreferenceChain(jcas);
		CoreferenceLink li = new CoreferenceLink(jcas);

		jcas.removeAllIncludingSubtypes(ch.getTypeIndexID());
		jcas.removeAllIncludingSubtypes(li.getTypeIndexID());

	}

}
