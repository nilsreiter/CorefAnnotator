package de.unistuttgart.ims.coref.annotator.plugins;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.coref.annotator.TypeSystemVersion;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.api.Meta;

public class CheckLoadability extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		TypeSystemVersion is = TypeSystemVersion.LEGACY;
		if (JCasUtil.exists(aJCas, Meta.class)) {
			Meta m = Util.getMeta(aJCas);
			if (m.getTypeSystemVersion() != null) {
				is = TypeSystemVersion.valueOf(m.getTypeSystemVersion());
				if (is == TypeSystemVersion.getCurrent())
					return;
			}
		}
		throw new AnalysisEngineProcessException("locales/strings", "message.wrong_file_version",
				new Object[] { is, TypeSystemVersion.getCurrent() });
	}

}
