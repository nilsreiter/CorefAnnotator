package de.unistuttgart.ims.coref.annotator.plugin.quadrama;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.ColorProvider;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Meta;

public class ImportQuaDramA extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		ColorProvider cm = new ColorProvider();

		TypeSystem ts = jcas.getTypeSystem();

		Type entityType = ts.getType(Constants.ENTITY_TYPE_NAME);
		Type mentionType = ts.getType(Constants.MENTION_TYPE_NAME);
		Type speakerType = ts.getType(Constants.TYPE_SPEAKER);

		Feature labelFeature = entityType.getFeatureByBaseName(Constants.ENTITY_LABEL_FEATURE_NAME);
		Feature entityFeature = mentionType.getFeatureByBaseName(Constants.MENTION_ENTITY_FEATURE_NAME);

		FSIterator<FeatureStructure> iter = jcas.getIndexRepository().getAllIndexedFS(entityType);

		Map<FeatureStructure, Entity> entityMap = new HashMap<FeatureStructure, Entity>();
		while (iter.hasNext()) {
			FeatureStructure fs = iter.next();
			Entity e = new Entity(jcas);
			e.setLabel(fs.getFeatureValueAsString(labelFeature));
			e.setColor(cm.getNextColor().getRGB());
			e.addToIndexes();
			entityMap.put(fs, e);
		}

		AnnotationIndex<Annotation> idx = jcas.getAnnotationIndex(mentionType);

		for (Annotation qm : idx) {
			FSArray eFS = (FSArray) qm.getFeatureValue(entityFeature);
			if (eFS != null)
				for (int i = 0; i < eFS.size(); i++) {
					de.unistuttgart.ims.coref.annotator.api.Mention m = AnnotationFactory.createAnnotation(jcas,
							qm.getBegin(), qm.getEnd(), de.unistuttgart.ims.coref.annotator.api.Mention.class);
					m.setEntity(entityMap.get(eFS.get(i)));

				}
		}
		entityFeature = speakerType.getFeatureByBaseName(Constants.SPEAKER_ENTITY_FEATURE_NAME);

		idx = jcas.getAnnotationIndex(speakerType);
		for (Annotation qm : idx) {
			FSArray eFS = (FSArray) qm.getFeatureValue(entityFeature);
			if (eFS != null)
				for (int i = 0; i < eFS.size(); i++) {
					de.unistuttgart.ims.coref.annotator.api.Mention m = AnnotationFactory.createAnnotation(jcas,
							qm.getBegin(), qm.getEnd(), de.unistuttgart.ims.coref.annotator.api.Mention.class);
					m.setEntity(entityMap.get(eFS.get(i)));
				}
		}

		Meta m = Util.getMeta(jcas);
		m.setStylePlugin(QDStylePlugin.class.getCanonicalName());

	}

}
