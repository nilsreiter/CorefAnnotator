package de.unistuttgart.ims.coref.annotator.uima;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;

import de.unistuttgart.ims.coref.annotator.FileFormat;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.api.v1_0.AnnotationComment;
import de.unistuttgart.ims.coref.annotator.api.v1_0.CommentAnchor;
import de.unistuttgart.ims.coref.annotator.api.v1_0.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.v1_0.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1_0.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.v1_0.Mention;

@TypeCapability(inputs = { "de.unistuttgart.ims.coref.annotator.api.Entity",
		"de.unistuttgart.ims.coref.annotator.api.EntityGroup", "de.unistuttgart.ims.coref.annotator.api.Mention",
		"de.unistuttgart.ims.coref.annotator.api.DetachedMentionPart",
		"de.unistuttgart.ims.coref.annotator.api.AnnotationComment" }, outputs = {
				"de.unistuttgart.ims.coref.annotator.api.v1_0.Entity",
				"de.unistuttgart.ims.coref.annotator.api.v1_0.EntityGroup",
				"de.unistuttgart.ims.coref.annotator.api.v1_0.Mention",
				"de.unistuttgart.ims.coref.annotator.api.v1_0.DetachedMentionPart",
				"de.unistuttgart.ims.coref.annotator.api.v1_0.AnnotationComment" })
public class MapAnnotation0To1 extends FileFormatVersionConverter {
	MutableMap<de.unistuttgart.ims.coref.annotator.api.Entity, Entity> entityMap = Maps.mutable.empty();
	MutableMap<de.unistuttgart.ims.coref.annotator.api.Mention, Mention> mentionMap = Maps.mutable.empty();
	MutableSet<TOP> toRemove = Sets.mutable.empty();

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

		// map entities and entity groups
		for (de.unistuttgart.ims.coref.annotator.api.Entity oldEntity : JCasUtil.select(jcas,
				de.unistuttgart.ims.coref.annotator.api.Entity.class)) {
			getEntity(jcas, oldEntity);
		}

		// map mentions and mention parts
		for (de.unistuttgart.ims.coref.annotator.api.Mention oldMention : JCasUtil.select(jcas,
				de.unistuttgart.ims.coref.annotator.api.Mention.class)) {
			getMention(jcas, oldMention);
		}

		// map comments
		for (de.unistuttgart.ims.coref.annotator.api.AnnotationComment aComment : JCasUtil.select(jcas,
				de.unistuttgart.ims.coref.annotator.api.AnnotationComment.class)) {
			getComment(jcas, aComment);
		}

		for (TOP fs : toRemove) {
			fs.removeFromIndexes();
		}

		Util.getMeta(jcas).setFileFormat(FileFormat.v1_0.name());
	}

	protected AnnotationComment getComment(JCas jcas,
			de.unistuttgart.ims.coref.annotator.api.AnnotationComment oldAnnotationComment) {
		AnnotationComment newAnnotationComment = new AnnotationComment(jcas);
		newAnnotationComment.addToIndexes();
		newAnnotationComment.setValue(oldAnnotationComment.getValue());
		newAnnotationComment.setAuthor(oldAnnotationComment.getAuthor());
		if (oldAnnotationComment.getAnnotation() != null) {
			Annotation oldAnchor = oldAnnotationComment.getAnnotation();
			CommentAnchor newAnchor = AnnotationFactory.createAnnotation(jcas, oldAnchor.getBegin(), oldAnchor.getEnd(),
					CommentAnchor.class);

			newAnnotationComment.setAnnotation(newAnchor);
			toRemove.add(oldAnchor);
		}
		toRemove.add(oldAnnotationComment);
		return newAnnotationComment;
	}

	protected Mention getMention(JCas jcas, de.unistuttgart.ims.coref.annotator.api.Mention oldMention) {
		if (!mentionMap.contains(oldMention)) {
			Mention mention;
			mention = new Mention(jcas);
			mention.setBegin(oldMention.getBegin());
			mention.setEnd(oldMention.getEnd());
			mention.addToIndexes();
			mention.setEntity(entityMap.get(oldMention.getEntity()));
			mention.setFlags(oldMention.getFlags());
			if (oldMention.getDiscontinuous() != null) {
				de.unistuttgart.ims.coref.annotator.api.DetachedMentionPart oldDmp = oldMention.getDiscontinuous();
				DetachedMentionPart dmp = new DetachedMentionPart(jcas);
				dmp.setBegin(oldDmp.getBegin());
				dmp.setEnd(oldDmp.getEnd());
				dmp.setMention(mention);
				dmp.addToIndexes();
				toRemove.add(oldDmp);
			}
			toRemove.add(oldMention);
		}
		return mentionMap.get(oldMention);
	}

	protected Entity getEntity(JCas jcas, de.unistuttgart.ims.coref.annotator.api.Entity oldEntity) {
		if (oldEntity == null)
			return null;
		if (!entityMap.contains(oldEntity)) {
			Entity newEntity;
			if (oldEntity instanceof de.unistuttgart.ims.coref.annotator.api.EntityGroup) {
				de.unistuttgart.ims.coref.annotator.api.EntityGroup oldEntityGroup = (de.unistuttgart.ims.coref.annotator.api.EntityGroup) oldEntity;
				EntityGroup newEntityG = new EntityGroup(jcas);
				newEntityG.setMembers(new FSArray(jcas, oldEntityGroup.getMembers().size()));
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
	public FileFormat getSource() {
		return FileFormat.LEGACY;
	}

	@Override
	public FileFormat getTarget() {
		return FileFormat.v1_0;
	}

}
