package de.unistuttgart.ims.coref.annotator.plugins.io.quadrama;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.coref.annotator.ColorMap;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.drama.api.DiscourseEntity;
import de.unistuttgart.ims.drama.api.Mention;
import de.unistuttgart.ims.drama.api.Speaker;

public class ImportQuaDramA extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		ColorMap cm = new ColorMap();

		Map<DiscourseEntity, Entity> entityMap = new HashMap<DiscourseEntity, Entity>();
		for (DiscourseEntity de : JCasUtil.select(jcas, DiscourseEntity.class)) {
			Entity e = new Entity(jcas);
			e.setLabel(de.getDisplayName());
			e.setColor(cm.getNextColor().getRGB());
			e.addToIndexes();
			entityMap.put(de, e);
		}

		for (Mention qm : JCasUtil.select(jcas, Mention.class)) {
			if (qm.getEntity() != null)
				for (int i = 0; i < qm.getEntity().size(); i++) {
					de.unistuttgart.ims.coref.annotator.api.Mention m = AnnotationFactory.createAnnotation(jcas,
							qm.getBegin(), qm.getEnd(), de.unistuttgart.ims.coref.annotator.api.Mention.class);
					m.setEntity(entityMap.get(qm.getEntity(i)));

				}
		}

		for (Speaker qm : JCasUtil.select(jcas, Speaker.class)) {
			if (qm.getCastFigure() != null)
				for (int i = 0; i < qm.getCastFigure().size(); i++) {
					de.unistuttgart.ims.coref.annotator.api.Mention m = AnnotationFactory.createAnnotation(jcas,
							qm.getBegin(), qm.getEnd(), de.unistuttgart.ims.coref.annotator.api.Mention.class);
					m.setEntity(entityMap.get(qm.getCastFigure(i)));
				}
		}

	}

}
