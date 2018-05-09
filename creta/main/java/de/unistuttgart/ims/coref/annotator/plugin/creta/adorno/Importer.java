package de.unistuttgart.ims.coref.annotator.plugin.creta.adorno;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.ColorProvider;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class Importer extends JCasAnnotator_ImplBase {

	ColorProvider colorMap = new ColorProvider();

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Map<String, Entity> entityMap = new HashMap<String, Entity>();

		Type mentionType = jcas.getTypeSystem().getType(Constants.mentionTypeName);
		Feature entityFeature = mentionType.getFeatureByBaseName(Constants.entityNameFeatureName);

		AnnotationIndex<Annotation> idx = jcas.getAnnotationIndex(mentionType);
		for (Annotation a : idx) {
			String entityString = a.getFeatureValueAsString(entityFeature);
			Mention m = AnnotationFactory.createAnnotation(jcas, a.getBegin(), a.getEnd(), Mention.class);
			if (!entityMap.containsKey(entityString)) {
				Entity e = new Entity(jcas);
				e.setLabel(entityString);
				e.setColor(colorMap.getNextColor().getRGB());
				e.addToIndexes();
				entityMap.put(entityString, e);
			}
			m.setEntity(entityMap.get(entityString));
		}
	}

}
