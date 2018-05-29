package de.unistuttgart.ims.coref.annotator.plugin.json;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasFlowController_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.flow.FinalStep;
import org.apache.uima.flow.Flow;
import org.apache.uima.flow.JCasFlow_ImplBase;
import org.apache.uima.flow.SimpleStep;
import org.apache.uima.flow.Step;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class FlowController extends JCasFlowController_ImplBase {

	@Override
	public Flow computeFlow(JCas jcas) throws AnalysisEngineProcessException {

		return new JCasFlow_ImplBase() {

			@Override
			public Step next() throws AnalysisEngineProcessException {
				if (!JCasUtil.exists(jcas, Token.class)) {
					return new SimpleStep(Constants.FLOW_KEY_TOKENIZER);
				}
				return new FinalStep();
			}

		};
	}

}
