package de.unistuttgart.ims.coref.annotator.uima;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.api.Meta;

/**
 * This component checks whether a Meta annotation exists. If there is none, we
 * create one and fill a few fields
 * 
 * @author reiterns
 *
 */
public class EnsureMeta extends JCasAnnotator_ImplBase {

	public static final String PARAM_TS_VERSION = "TS_VERSION";

	@ConfigurationParameter(name = PARAM_TS_VERSION, mandatory = false, defaultValue = "v2")
	String typeSystemVersion;

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		if (!JCasUtil.exists(aJCas, Meta.class)) {
			Meta meta = new Meta(aJCas);
			meta.setTypeSystemVersion(typeSystemVersion);
			meta.setStylePlugin(Annotator.app.getPluginManager().getDefaultStylePlugin().getClass().getName());
			meta.addToIndexes();
		}

	}

}
