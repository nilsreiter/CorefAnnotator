package de.unistuttgart.ims.coref.annotator.plugin.conll2012;

import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.impl.factory.Maps;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class Exporter extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		Type chainType = jcas.getTypeSystem().getType(Constants.TYPE_CHAIN);
		Type linkType = jcas.getTypeSystem().getType(Constants.TYPE_LINK);

		Feature firstFeature = chainType.getFeatureByBaseName(Constants.FEATURE_FIRST);
		Feature nextFeature = linkType.getFeatureByBaseName(Constants.FEATURE_NEXT);

		Map<Entity, FeatureStructure> next = Maps.mutable.empty();
		for (Entity e : JCasUtil.select(jcas, Entity.class)) {
			FeatureStructure chain = jcas.getCas().createFS(chainType);
			jcas.addFsToIndexes(chain);
			next.put(e, chain);
		}

		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			AnnotationFS a = jcas.getCas().createAnnotation(linkType, m.getBegin(), m.getEnd());
			jcas.addFsToIndexes(a);
			FeatureStructure prev = next.get(m.getEntity());
			if (prev.getType() == chainType) {
				prev.setFeatureValue(firstFeature, a);
			} else if (prev.getType() == linkType) {
				prev.setFeatureValue(nextFeature, a);
			}
			next.put(m.getEntity(), a);
		}
	}

}
