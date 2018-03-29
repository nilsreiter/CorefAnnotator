package de.unistuttgart.ims.coref.annotator.plugins;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;

public class MergeAnnotations extends JCasAnnotator_ImplBase {

	public static final String PARAM_INPUT = "Input file";

	@ConfigurationParameter(name = PARAM_INPUT)
	String fileName;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			JCas jcas2 = JCasFactory.createJCas(fileName, TypeSystemDescriptionFactory.createTypeSystemDescription());

			MutableMap<Entity, Entity> entityMap = Maps.mutable.empty();

			// handle entities
			for (Entity oldEntity : JCasUtil.select(jcas2, Entity.class)) {
				Entity newEntity;
				if (oldEntity instanceof EntityGroup) {
					newEntity = new EntityGroup(jcas);
				} else {
					newEntity = new Entity(jcas);
				}
				newEntity.addToIndexes();
				entityMap.put(oldEntity, newEntity);
			}

			// handle entity groups
			for (EntityGroup oldEntity : JCasUtil.select(jcas2, EntityGroup.class)) {
				EntityGroup newEntity = (EntityGroup) entityMap.get(oldEntity);
				FSArray arr = new FSArray(jcas, oldEntity.getMembers().size());
				arr.addToIndexes();
				newEntity.setMembers(arr);
				for (int i = 0; i < oldEntity.getMembers().size(); i++) {
					newEntity.setMembers(i, entityMap.get(oldEntity.getMembers(i)));
				}
			}

		} catch (UIMAException | IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}
