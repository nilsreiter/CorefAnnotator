package de.unistuttgart.ims.coref.annotator.plugins;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasFlowController_ImplBase;
import org.apache.uima.flow.FinalStep;
import org.apache.uima.flow.Flow;
import org.apache.uima.flow.JCasFlow_ImplBase;
import org.apache.uima.flow.SimpleStep;
import org.apache.uima.flow.Step;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.coref.annotator.TypeSystemVersion;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.api.Meta;

public class ConvertFlowController extends JCasFlowController_ImplBase {

	@Override
	public Flow computeFlow(JCas jcas) throws AnalysisEngineProcessException {
		return new JCasFlow_ImplBase() {
			@Override
			public Step next() throws AnalysisEngineProcessException {
				Meta metaData = Util.getMeta(jcas);
				if (metaData.getTypeSystemVersion() == null) {
					return new SimpleStep(TypeSystemVersion.v1.name());
				} else {
					TypeSystemVersion tsVersion = TypeSystemVersion.valueOf(metaData.getTypeSystemVersion());

					// add new versions to this switch statement
					switch (tsVersion) {
					case LEGACY:
						return new SimpleStep(TypeSystemVersion.v1.name());
					case v1:
					default:
						return new FinalStep();
					}
				}

			};
		};
	};

}
