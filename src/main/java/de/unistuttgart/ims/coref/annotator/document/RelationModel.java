package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.fit.util.JCasUtil;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Sets;

import de.unistuttgart.ims.coref.annotator.api.v1.DirectedEntityRelation;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityRelation;
import de.unistuttgart.ims.coref.annotator.api.v1.SymmetricEntityRelation;
import de.unistuttgart.ims.coref.annotator.document.op.RelateEntities;
import de.unistuttgart.ims.coref.annotator.document.op.RelationModelOperation;
import de.unistuttgart.ims.uima.io.xml.ArrayUtil;

public class RelationModel {
	DocumentModel documentModel;

	public RelationModel(DocumentModel documentModel) {
		this.documentModel = documentModel;
	}

	public ImmutableSet<EntityRelation> getRelations() {
		return Sets.immutable.withAll(JCasUtil.select(documentModel.getJcas(), EntityRelation.class));
	}

	protected void edit(RelationModelOperation op) {

	}

	protected void edit(RelateEntities op) {
		EntityRelation erel;
		if (op.getRelationType().getDirected()) {
			DirectedEntityRelation rel = new DirectedEntityRelation(documentModel.getJcas());
			rel.setSource(op.getEntities().get(0));
			rel.setTarget(op.getEntities().get(1));
			erel = rel;
		} else {
			SymmetricEntityRelation rel = new SymmetricEntityRelation(documentModel.getJcas());
			rel.setEntities(ArrayUtil.toFSArray(documentModel.getJcas(), rel.getEntities()));
			erel = rel;
		}
		erel.setRelationType(op.getRelationType());
		erel.addToIndexes();
		op.setRelation(erel);
	}

	protected void undo(RelationModelOperation op) {

	}

	protected void undo(RelateEntities op) {
		op.getRelation().removeFromIndexes();
	}
}
