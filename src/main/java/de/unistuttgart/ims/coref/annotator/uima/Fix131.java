package de.unistuttgart.ims.coref.annotator.uima;

import java.util.Collection;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Sets;

import  de.unistuttgart.ims.coref.annotator.api.v2.DetachedMentionPart;
import  de.unistuttgart.ims.coref.annotator.api.v2.Mention;

/**
 * Component to fix errors that result from conversion issues
 * 
 * @author reiterns
 *
 */
public class Fix131 extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		MutableSet<Annotation> toRemove = Sets.mutable.empty();
		Collection<DetachedMentionPart> parts = JCasUtil.select(jcas, DetachedMentionPart.class);
		for (DetachedMentionPart part : parts) {
			if (part.getMention() == null) {
				for (Mention m : JCasUtil.select(jcas, Mention.class))
					if (m.getDiscontinuous() == part)
						part.setMention(m);
				if (part.getMention() == null)
					toRemove.add(part);
			} else if (part.getMention().getDiscontinuous() != part) {
				part.getMention().setDiscontinuous(part);
			}
		}
	}

}
