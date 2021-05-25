package de.unistuttgart.ims.coref.annotator.uima;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

public class SetJCasLanguage extends JCasAnnotator_ImplBase {

	public static final String PARAM_LANGUAGE = "Language";

	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = true)
	String language = null;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		jcas.setDocumentLanguage(language);
	}

}
