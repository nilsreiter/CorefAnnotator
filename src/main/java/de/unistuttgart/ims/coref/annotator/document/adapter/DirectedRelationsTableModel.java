package de.unistuttgart.ims.coref.annotator.document.adapter;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.api.v1.DirectedEntityRelation;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityRelation;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;
import de.unistuttgart.ims.coref.annotator.document.RelationModel;
import de.unistuttgart.ims.coref.annotator.document.RelationModelListener;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateDirectedEntityRelation;

public class DirectedRelationsTableModel extends DefaultTableModel implements TableModel, RelationModelListener {

	/**
	 * 
	 */
	private RelationModel relationModel;

	/**
	 * @param relationModel
	 */
	public DirectedRelationsTableModel(RelationModel relationModel) {
		this.relationModel = relationModel;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public int getRowCount() {
		return this.relationModel.getDocumentModel().getRelationModel().getRelations()
				.select(r -> r instanceof DirectedEntityRelation).size();
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
		EntityRelation er = this.relationModel.getRelations().get(rowIndex);

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
		DirectedEntityRelation erel = (DirectedEntityRelation) this.relationModel.getRelations().get(rowIndex);
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
		this.relationModel.getDocumentModel().edit(new UpdateDirectedEntityRelation(erel, property, aValue));
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
			tme = new TableModelEvent(this, this.relationModel.getRelations().indexOf(event.getArgument(0)));
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