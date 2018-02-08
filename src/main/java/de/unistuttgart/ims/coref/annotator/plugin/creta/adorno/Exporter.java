package de.unistuttgart.ims.coref.annotator.plugin.creta.adorno;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.coref.annotator.api.Mention;

public class Exporter extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		CAS cas;
		cas = jcas.getCas();
		Type mentionType = jcas.getTypeSystem().getType(Constants.mentionTypeName);
		Feature entityFeature = mentionType.getFeatureByBaseName(Constants.entityNameFeatureName);

		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			AnnotationFS a = cas.createAnnotation(mentionType, m.getBegin(), m.getEnd());
			cas.addFsToIndexes(a);
			a.setFeatureValueFromString(entityFeature, m.getEntity().getLabel());
		}

	}

}
