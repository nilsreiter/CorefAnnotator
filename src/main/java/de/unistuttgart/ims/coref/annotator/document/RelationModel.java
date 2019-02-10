package de.unistuttgart.ims.coref.annotator.document;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.TableModel;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.cas.FSArray;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.api.v1.DirectedEntityRelation;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityRelation;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityRelationType;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.api.v1.SymmetricEntityRelation;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel.EntitySorter;
import de.unistuttgart.ims.coref.annotator.document.adapter.DirectedRelationsTableModel;
import de.unistuttgart.ims.coref.annotator.document.op.AddDirectedRelation;
import de.unistuttgart.ims.coref.annotator.document.op.AddUndirectedRelation;
import de.unistuttgart.ims.coref.annotator.document.op.RelateEntities;
import de.unistuttgart.ims.coref.annotator.document.op.RelationModelOperation;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateEntityRelation;
import de.unistuttgart.ims.uima.io.xml.ArrayUtil;

public class RelationModel implements Model, ListModel<EntityRelation> {
	DocumentModel documentModel;

	DirectedRelationsTableModel directedRelationsTableModel = null, undirectedRelationsTableModel = null;

	MutableList<EntityRelation> list = Lists.mutable.empty();

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

	protected void edit(RelationModelOperation op) {
		if (op instanceof RelateEntities)
			edit((RelateEntities) op);
		else if (op instanceof AddDirectedRelation)
			edit((AddDirectedRelation) op);
		else if (op instanceof AddUndirectedRelation)
			edit((AddUndirectedRelation) op);
		else if (op instanceof UpdateEntityRelation)
			edit((UpdateEntityRelation) op);
		else
			throw new UnsupportedOperationException();
	}

	protected void edit(AddDirectedRelation op) {
		DirectedEntityRelation erel = new DirectedEntityRelation(documentModel.getJcas());
		op.setEntityRelation(erel);
		erel.addToIndexes();
		list.add(erel);
		fireRelationEvent(Event.get(this, Event.Type.Add, erel));
	}

	protected void edit(AddUndirectedRelation op) {
		SymmetricEntityRelation erel = new SymmetricEntityRelation(documentModel.getJcas());
		erel.setEntities(new FSArray(documentModel.getJcas(), 2));
		erel.setEntities(0, documentModel.getCoreferenceModel().getEntities(EntitySorter.COLOR).getFirst());
		erel.setEntities(1, documentModel.getCoreferenceModel().getEntities(EntitySorter.COLOR).getLast());
		op.setEntityRelation(erel);
		erel.addToIndexes();
		list.add(erel);
		fireRelationEvent(Event.get(this, Event.Type.Add, erel));
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

	protected void edit(UpdateEntityRelation op) {
		EntityRelation reln = op.getObjects().getFirst();
		if (reln instanceof DirectedEntityRelation) {
			DirectedEntityRelation der = (DirectedEntityRelation) reln;
			switch (op.getEntityRelationProperty()) {
			case SOURCE:
				op.setOldValue(der.getSource());
				der.setSource((Entity) op.getNewValue());
				break;
			case TARGET:
				op.setOldValue(der.getTarget());
				der.setTarget((Entity) op.getNewValue());
				break;
			case TYPE:
				op.setOldValue(der.getRelationType());
				der.setFlag((Flag) op.getNewValue());
				break;
			default:
				break;

			}
		} else if (reln instanceof SymmetricEntityRelation) {
			SymmetricEntityRelation ser = (SymmetricEntityRelation) reln;
			switch (op.getEntityRelationProperty()) {
			case ADD_ENTITY:
				if (Util.contains(ser.getEntities(), (Entity) op.getNewValue()))
					return;
				op.setOldValue(ser.getEntities());
				FSArray newArray = Util.addTo(documentModel.getJcas(), ser.getEntities(), (Entity) op.getNewValue());
				ser.setEntities(newArray);
				break;
			case REMOVE_ENTITY:
				break;
			case TYPE:
				op.setOldValue(ser.getRelationType());
				ser.setFlag((Flag) op.getNewValue());
				break;
			default:
				break;

			}
		}
		fireRelationEvent(Event.get(this, Event.Type.Update, reln));
	}

	private void fireRelationEvent(FeatureStructureEvent evt) {
		listeners.forEach(l -> l.relationEvent(evt));
	}

	public ImmutableList<EntityRelation> getRelations() {
		return list.toImmutable();
	}

	@Deprecated
	public ImmutableList<EntityRelationType> getRelationTypes() {
		return Lists.immutable.withAll(JCasUtil.select(documentModel.getJcas(), EntityRelationType.class));
	}

	public ImmutableList<Flag> getRelationFlags(Class<? extends EntityRelation> cl) {
		return documentModel.getFlagModel().getFlags(cl);
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

	@Deprecated
	public TableModel getDirectedRelationsTableModel() {
		if (directedRelationsTableModel == null) {
			directedRelationsTableModel = new DirectedRelationsTableModel(this);
			addRelationModelListener(directedRelationsTableModel);
		}
		return directedRelationsTableModel;
	}

	@Deprecated
	public TableModel getUndirectedRelationsTableModel() {
		if (undirectedRelationsTableModel == null) {
			undirectedRelationsTableModel = new DirectedRelationsTableModel(this);
			addRelationModelListener(undirectedRelationsTableModel);
		}
		return undirectedRelationsTableModel;
	}

	public DocumentModel getDocumentModel() {
		return documentModel;
	}
}
