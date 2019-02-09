package de.unistuttgart.ims.coref.annotator.document;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.cas.FSArray;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.api.v1.DirectedEntityRelation;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityRelation;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityRelationType;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.api.v1.SymmetricEntityRelation;
import de.unistuttgart.ims.coref.annotator.document.op.AddDirectedRelation;
import de.unistuttgart.ims.coref.annotator.document.op.AddUndirectedRelation;
import de.unistuttgart.ims.coref.annotator.document.op.RelateEntities;
import de.unistuttgart.ims.coref.annotator.document.op.RelationModelOperation;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateDirectedEntityRelation;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateUndirectedEntityRelation;
import de.unistuttgart.ims.uima.io.xml.ArrayUtil;

public class RelationModel implements Model, ListModel<EntityRelation> {
	DocumentModel documentModel;

	DirectedRelationsTableModel directedRelationsTableModel = null, undirectedRelationsTableModel = null;

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

	protected void edit(RelationModelOperation op) {
		if (op instanceof RelateEntities)
			edit((RelateEntities) op);
		else if (op instanceof AddDirectedRelation)
			edit((AddDirectedRelation) op);
		else if (op instanceof AddUndirectedRelation)
			edit((AddUndirectedRelation) op);
		else if (op instanceof UpdateDirectedEntityRelation)
			edit((UpdateDirectedEntityRelation) op);
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
		erel.setEntities(new FSArray(documentModel.getJcas(), 0));
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

	protected void edit(UpdateDirectedEntityRelation op) {
		DirectedEntityRelation der = op.getObjects().getFirst();
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
		fireRelationEvent(Event.get(this, Event.Type.Update, der));
	}

	private void fireRelationEvent(FeatureStructureEvent evt) {
		listeners.forEach(l -> l.relationEvent(evt));
	}

	public ImmutableList<EntityRelation> getRelations() {
		return list.toImmutable();
	}

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

	public TableModel getDirectedRelationsTableModel() {
		if (directedRelationsTableModel == null) {
			directedRelationsTableModel = new DirectedRelationsTableModel();
			addRelationModelListener(directedRelationsTableModel);
		}
		return directedRelationsTableModel;
	}

	public TableModel getUndirectedRelationsTableModel() {
		if (undirectedRelationsTableModel == null) {
			undirectedRelationsTableModel = new DirectedRelationsTableModel();
			addRelationModelListener(undirectedRelationsTableModel);
		}
		return undirectedRelationsTableModel;
	}

	class UndirectedRelationsTableModel extends DefaultTableModel implements TableModel, RelationModelListener {

		private static final long serialVersionUID = 1L;

		@Override
		public int getRowCount() {
			return documentModel.getRelationModel().getRelations().select(r -> r instanceof SymmetricEntityRelation)
					.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(int columnIndex) {
			// TODO: strings
			switch (columnIndex) {
			case 0:
				return "Relation";
			case 1:
				return "Entities";
			}
			return null;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return Flag.class;
			case 1:
				return FSArray.class;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			EntityRelation er = list.get(rowIndex);

			if (er instanceof SymmetricEntityRelation) {
				SymmetricEntityRelation der = (SymmetricEntityRelation) er;
				switch (columnIndex) {
				case 0:
					return der.getFlag();
				case 1:
					return der.getEntities();
				}
				return null;
			}
			return null;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			SymmetricEntityRelation erel = (SymmetricEntityRelation) list.get(rowIndex);
			UpdateUndirectedEntityRelation.EntityRelationProperty property = null;
			switch (columnIndex) {
			case 0:
				property = UpdateUndirectedEntityRelation.EntityRelationProperty.TYPE;
				break;
			case 1:
				property = UpdateUndirectedEntityRelation.EntityRelationProperty.ENTITIES;
				break;
			}
			documentModel.edit(new UpdateUndirectedEntityRelation(erel, property, aValue));
		}

		@Override
		public void relationEvent(FeatureStructureEvent event) {
			TableModelEvent tme = null;

			switch (event.getType()) {
			case Add:
				tme = new TableModelEvent(this, getRowCount() - 1, getRowCount() - 1, TableModelEvent.ALL_COLUMNS,
						TableModelEvent.INSERT);
				break;
			case Merge:
				break;
			case Move:
				break;
			case Op:
				break;
			case Remove:
				break;
			case Update:
				tme = new TableModelEvent(this, list.indexOf(event.getArgument(0)));
				break;
			default:
				break;

			}
			Annotator.logger.debug(tme);
			if (tme != null)
				for (TableModelListener l : this.getTableModelListeners())
					l.tableChanged(tme);

		}

	}

	class DirectedRelationsTableModel extends DefaultTableModel implements TableModel, RelationModelListener {

		private static final long serialVersionUID = 1L;

		@Override
		public int getRowCount() {
			return documentModel.getRelationModel().getRelations().select(r -> r instanceof DirectedEntityRelation)
					.size();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return "Source";
			case 1:
				return "Target";
			case 2:
				return "Relation";
			}
			return null;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return Entity.class;
			case 1:
				return Entity.class;
			case 2:
				return Flag.class;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			EntityRelation er = list.get(rowIndex);

			if (er instanceof DirectedEntityRelation) {
				DirectedEntityRelation der = (DirectedEntityRelation) er;
				switch (columnIndex) {
				case 0:
					return der.getSource();
				case 1:
					return der.getTarget();
				case 2:
					return der.getFlag();
				}
				return null;
			}
			return null;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			DirectedEntityRelation erel = (DirectedEntityRelation) list.get(rowIndex);
			UpdateDirectedEntityRelation.EntityRelationProperty property = null;
			switch (columnIndex) {
			case 0:
				property = UpdateDirectedEntityRelation.EntityRelationProperty.SOURCE;
				break;
			case 1:
				property = UpdateDirectedEntityRelation.EntityRelationProperty.TARGET;
				break;
			case 2:
				property = UpdateDirectedEntityRelation.EntityRelationProperty.TYPE;
				break;
			}
			documentModel.edit(new UpdateDirectedEntityRelation(erel, property, aValue));
		}

		@Override
		public void relationEvent(FeatureStructureEvent event) {
			TableModelEvent tme = null;

			switch (event.getType()) {
			case Add:
				tme = new TableModelEvent(this, getRowCount() - 1, getRowCount() - 1, TableModelEvent.ALL_COLUMNS,
						TableModelEvent.INSERT);
				break;
			case Merge:
				break;
			case Move:
				break;
			case Op:
				break;
			case Remove:
				break;
			case Update:
				tme = new TableModelEvent(this, list.indexOf(event.getArgument(0)));
				break;
			default:
				break;

			}
			Annotator.logger.debug(tme);
			if (tme != null)
				for (TableModelListener l : this.getTableModelListeners())
					l.tableChanged(tme);

		}

	}
}
