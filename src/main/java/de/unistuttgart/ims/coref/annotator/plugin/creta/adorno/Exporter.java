package de.unistuttgart.ims.coref.annotator.plugin.creta.adorno;

import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
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
		CAS cas;
		cas = jcas.getCas();
		Type mentionType = jcas.getTypeSystem().getType(Constants.mentionTypeName);
		Feature entityFeature = mentionType.getFeatureByBaseName(Constants.entityNameFeatureName);

		Map<Entity, String> entityLabelMap = Maps.mutable.empty();

		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			AnnotationFS a = cas.createAnnotation(mentionType, m.getBegin(), m.getEnd());
			cas.addFsToIndexes(a);
			if (!entityLabelMap.containsKey(m.getEntity())) {
				String label = m.getEntity().getLabel();
				int lNum = 0;
				while (entityLabelMap.values().contains(label)
						|| entityLabelMap.values().contains(label + "." + lNum)) {
					// label collision
					lNum++;
				}
				entityLabelMap.put(m.getEntity(), (lNum > 0 ? label + "." + lNum : label));
			}
			a.setFeatureValueFromString(entityFeature, entityLabelMap.get(m.getEntity()));
		}

	}

}
