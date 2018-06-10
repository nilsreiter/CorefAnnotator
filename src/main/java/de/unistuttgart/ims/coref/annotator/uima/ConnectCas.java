package de.unistuttgart.ims.coref.annotator.uima;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

public class ConnectCas extends JCasAnnotator_ImplBase {
	public static final String PARAM_INPUT = "Input file";

	@ConfigurationParameter(name = PARAM_INPUT)
	String fileName;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		getLogger().debug("Processing file " + fileName);

	}

}
