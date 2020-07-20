package de.unistuttgart.ims.coref.annotator.plugin.versions.v1;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.TypeSystemVersion;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.api.Meta;
import de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import de.unistuttgart.ims.coref.annotator.api.v2.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.api.v2.MentionSurface;
import de.unistuttgart.ims.coref.annotator.uima.TypeSystemVersionConverter;
import de.unistuttgart.ims.coref.annotator.uima.UimaUtil;

@TypeCapability(inputs = { "de.unistuttgart.ims.coref.annotator.api.v1.Entity",
		"de.unistuttgart.ims.coref.annotator.api.v1.EntityGroup", "de.unistuttgart.ims.coref.annotator.api.v1.Mention",
		"de.unistuttgart.ims.coref.annotator.api.v1.DetachedMentionPart" }, outputs = {
				"de.unistuttgart.ims.coref.annotator.api.v2.Entity",
				"de.unistuttgart.ims.coref.annotator.api.v2.EntityGroup",
				"de.unistuttgart.ims.coref.annotator.api.v2.Mention",
				"de.unistuttgart.ims.coref.annotator.api.v2.MentionSurface",
				"de.unistuttgart.ims.coref.annotator.api.v2.DetachedMentionPart" })
public class V1_To_V2 extends TypeSystemVersionConverter {
	MutableMap<de.unistuttgart.ims.coref.annotator.api.v1.Entity, Entity> entityMap = Maps.mutable.empty();
	MutableMap<de.unistuttgart.ims.coref.annotator.api.v1.Mention, Mention> mentionMap = Maps.mutable.empty();
	MutableSet<TOP> toRemove = Sets.mutable.empty();

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		Annotator.logger.info("Converting type system from v1 to v2");

		// map entities and entity groups
		for (de.unistuttgart.ims.coref.annotator.api.v1.Entity oldEntity : JCasUtil.select(jcas,
				de.unistuttgart.ims.coref.annotator.api.v1.Entity.class)) {
			getEntity(jcas, oldEntity);
		}

		// map mentions and mention parts
		for (de.unistuttgart.ims.coref.annotator.api.v1.Mention oldMention : JCasUtil.select(jcas,
				de.unistuttgart.ims.coref.annotator.api.v1.Mention.class)) {
			getMention(jcas, oldMention);
		}

		for (TOP fs : toRemove) {
			fs.removeFromIndexes();
		}

		Meta meta = Util.getMeta(jcas);
		meta.setTypeSystemVersion(TypeSystemVersion.v2.name());
	}

	protected Mention getMention(JCas jcas, de.unistuttgart.ims.coref.annotator.api.v1.Mention oldMention) {
		if (!mentionMap.containsKey(oldMention)) {
			Mention mention = UimaUtil.getMention(jcas, oldMention.getBegin(), oldMention.getEnd());
			mention.addToIndexes();
			mention.setEntity(getEntity(jcas, oldMention.getEntity()));

			// this is a bit hacky, but for some reason a mention doesn't have an entity in
			// an old file
			// Disabled
			if (false && mention.getEntity() == null) {
				Entity newEntity = new Entity(jcas);
				newEntity.addToIndexes();
				newEntity.setLabel(UimaUtil.getCoveredText(mention));
				mention.setEntity(newEntity);
			}
			mention.setFlags(oldMention.getFlags());
			if (oldMention.getDiscontinuous() != null) {
				MentionSurface ms = AnnotationFactory.createAnnotation(jcas, oldMention.getDiscontinuous().getBegin(),
						oldMention.getDiscontinuous().getEnd(), MentionSurface.class);
				ms.setMention(mention);
				UimaUtil.addMentionSurface(mention, ms);

				de.unistuttgart.ims.coref.annotator.api.v1.DetachedMentionPart oldDmp = oldMention.getDiscontinuous();
				toRemove.add(oldDmp);
			}
			mentionMap.put(oldMention, mention);
			toRemove.add(oldMention);
		}
		return mentionMap.get(oldMention);
	}

	protected Entity getEntity(JCas jcas, de.unistuttgart.ims.coref.annotator.api.v1.Entity oldEntity) {
		if (oldEntity == null)
			return null;
		if (!entityMap.containsKey(oldEntity)) {
			Entity newEntity;
			if (oldEntity instanceof de.unistuttgart.ims.coref.annotator.api.v1.EntityGroup) {
				de.unistuttgart.ims.coref.annotator.api.v1.EntityGroup oldEntityGroup = (de.unistuttgart.ims.coref.annotator.api.v1.EntityGroup) oldEntity;
				EntityGroup newEntityG = new EntityGroup(jcas);
				newEntityG.setMembers(new FSArray<Entity>(jcas, oldEntityGroup.getMembers().size()));
				newEntityG.getMembers().addToIndexes();
				for (int i = 0; i < newEntityG.getMembers().size(); i++) {
					newEntityG.setMembers(i, getEntity(jcas, oldEntityGroup.getMembers(i)));
				}
				newEntity = newEntityG;
			} else {
				newEntity = new Entity(jcas);
			}
			newEntity.addToIndexes();
			if (oldEntity.getLabel() != null)
				newEntity.setLabel(oldEntity.getLabel());
			newEntity.setFlags(oldEntity.getFlags());
			newEntity.setColor(oldEntity.getColor());
			newEntity.setKey(oldEntity.getKey());
			newEntity.setXmlId(oldEntity.getXmlId());
			newEntity.setHidden(oldEntity.getHidden());

			entityMap.put(oldEntity, newEntity);
			toRemove.add(oldEntity);
		}
		return entityMap.get(oldEntity);
	}

	@Override
	public TypeSystemVersion getSource() {
		return TypeSystemVersion.v1;
	}

	@Override
	public TypeSystemVersion getTarget() {
		return TypeSystemVersion.v2;
	}

}
