package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.fit.util.JCasUtil;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Sets;

import de.unistuttgart.ims.coref.annotator.api.v1.EntityRelation;
import de.unistuttgart.ims.coref.annotator.document.op.RelateEntities;

public class RelationModel {
	DocumentModel documentModel;

	public RelationModel(DocumentModel documentModel) {
		this.documentModel = documentModel;
	}

	public ImmutableSet<EntityRelation> getRelations() {
		return Sets.immutable.withAll(JCasUtil.select(documentModel.getJcas(), EntityRelation.class));
	}

	protected void edit(RelateEntities op) {

	}
}
