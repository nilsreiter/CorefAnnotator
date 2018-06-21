package de.unistuttgart.ims.coref.annotator.document;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.uima.fit.util.JCasUtil;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v1.DirectedEntityRelation;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityRelation;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityRelationType;
import de.unistuttgart.ims.coref.annotator.api.v1.SymmetricEntityRelation;
import de.unistuttgart.ims.coref.annotator.document.op.Operation;
import de.unistuttgart.ims.coref.annotator.document.op.RelateEntities;
import de.unistuttgart.ims.coref.annotator.document.op.RelationModelOperation;
import de.unistuttgart.ims.uima.io.xml.ArrayUtil;

public class RelationModel implements ListModel<EntityRelation> {
	DocumentModel documentModel;

	private MutableList<EntityRelation> list = Lists.mutable.empty();

	MutableList<RelationModelListener> listeners = Lists.mutable.empty();
	MutableList<ListDataListener> listDataListeners = Lists.mutable.empty();

	public RelationModel(DocumentModel documentModel) {
		this.documentModel = documentModel;
		list.addAll(JCasUtil.select(documentModel.getJcas(), EntityRelation.class));

		if (!JCasUtil.exists(documentModel.getJcas(), EntityRelationType.class)) {
			EntityRelationType ert = new EntityRelationType(documentModel.getJcas());
			ert.setLabel("parent of");
			ert.setDirected(true);
			ert.addToIndexes();
		}
	}

	public boolean addRelationModelListener(RelationModelListener e) {
		return listeners.add(e);
	}

	protected void edit(Operation op) {
		if (op instanceof RelateEntities)
			edit((RelateEntities) op);
		else
			throw new UnsupportedOperationException();
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
		list.add(erel);

		ListDataEvent lde = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, list.size() - 1, list.size());
		listDataListeners.forEach(l -> l.intervalAdded(lde));
	}

	public ImmutableList<EntityRelation> getRelations() {
		return list.toImmutable();
	}

	public ImmutableList<EntityRelationType> getRelationTypes() {
		return Lists.immutable.withAll(JCasUtil.select(documentModel.getJcas(), EntityRelationType.class));
	}

	public boolean removeRelationModelListener(Object o) {
		return listeners.remove(o);
	}

	protected void undo(RelationModelOperation op) {

	}

	protected void undo(RelateEntities op) {
		op.getRelation().removeFromIndexes();
	}

	@Override
	public int getSize() {
		return getRelations().size();
	}

	@Override
	public EntityRelation getElementAt(int index) {
		return getRelations().get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listDataListeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listDataListeners.remove(l);
	}
}
