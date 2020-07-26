package de.unistuttgart.ims.coref.annotator.uima;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.EmptyFSList;
import org.apache.uima.jcas.cas.FSArray;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;
import org.xml.sax.SAXException;

import de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import de.unistuttgart.ims.coref.annotator.api.v2.Flag;
import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.api.v2.MentionSurface;

public class MergeAnnotations extends JCasAnnotator_ImplBase {

	public static final String PARAM_INPUT = "Input file";

	@ConfigurationParameter(name = PARAM_INPUT)
	String fileName;

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		getLogger().debug("Processing file " + fileName);
		try {

			JCas jcas2 = UimaUtil.readJCas(fileName);// JCasFactory.createJCas(fileName,
														// TypeSystemDescriptionFactory.createTypeSystemDescription());

			MutableMap<Entity, Entity> entityMap = Maps.mutable.empty();

			// handle entities
			for (Entity oldEntity : JCasUtil.select(jcas2, Entity.class)) {
				Entity newEntity;

				newEntity = new Entity(jcas);
				newEntity.setLabel(oldEntity.getLabel());
				newEntity.setColor(oldEntity.getColor());
				newEntity.addToIndexes();
				entityMap.put(oldEntity, newEntity);
				newEntity.setFlags(new EmptyFSList<Flag>(jcas));

				if (oldEntity.getFlags() != null) {
					for (Flag flag : oldEntity.getFlags())
						newEntity.getFlags().push(flag);
				}

				if (UimaUtil.isGroup(oldEntity)) {
					FSArray<Entity> arr = new FSArray<Entity>(jcas, oldEntity.getMembers().size());
					arr.addToIndexes();
					newEntity.setMembers(arr);
					for (int i = 0; i < oldEntity.getMembers().size(); i++) {
						newEntity.setMembers(i, entityMap.get(oldEntity.getMembers(i)));
					}
				}

			}

			// handle mentions
			for (Mention m : JCasUtil.select(jcas2, Mention.class)) {
				Mention newMention = new Mention(jcas);
				newMention.setSurface(new FSArray<MentionSurface>(jcas, m.getSurface().size()));
				int i = 0;
				for (MentionSurface ms : m.getSurface()) {
					MentionSurface newMS = AnnotationFactory.createAnnotation(jcas, ms.getBegin(), ms.getEnd(),
							MentionSurface.class);
					newMS.setMention(newMention);
					newMention.setSurface(i++, newMS);
				}
				newMention.setEntity(entityMap.get(m.getEntity()));
				newMention.addToIndexes();
				newMention.setFlags(new EmptyFSList<Flag>(jcas));

				if (m.getFlags() != null) {
					for (Flag flag : m.getFlags())
						newMention.getFlags().push(flag);

				}
			}

		} catch (UIMAException | IOException | SAXException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}
