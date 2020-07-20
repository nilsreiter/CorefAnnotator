package de.unistuttgart.ims.coref.annotator.plugin.dkpro;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceChain;
import de.tudarmstadt.ukp.dkpro.core.api.coref.type.CoreferenceLink;
import de.unistuttgart.ims.coref.annotator.ColorProvider;
import de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.uima.UimaUtil;

public class ImportDKpro extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		ColorProvider cp = new ColorProvider();
		for (CoreferenceChain cc : JCasUtil.select(jcas, CoreferenceChain.class)) {
			Entity e = new Entity(jcas);
			e.addToIndexes();
			e.setColor(cp.getNextColor().getRGB());
			CoreferenceLink link = cc.getFirst();
			int maxLength = 0;
			String maxLabel = null;
			while (link != null) {
				Mention m = UimaUtil.getMention(jcas, link.getBegin(), link.getEnd());
				m.setEntity(e);
				if (link.getCoveredText().length() > maxLength) {
					maxLabel = link.getCoveredText();
					maxLength = maxLabel.length();
				}
				link = link.getNext();
			}
			if (maxLabel != null)
				e.setLabel(maxLabel);
		}

	}

}
