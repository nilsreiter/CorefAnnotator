package de.unistuttgart.ims.coref.annotator.document.adapter;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.uima.jcas.cas.FSArray;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityRelation;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.api.v1.SymmetricEntityRelation;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;
import de.unistuttgart.ims.coref.annotator.document.RelationModel;
import de.unistuttgart.ims.coref.annotator.document.RelationModelListener;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateDirectedEntityRelation;

public class UndirectedRelationsTableModel extends DefaultTableModel implements TableModel, RelationModelListener {

	/**
	 * 
	 */
	private RelationModel relationModel;

	/**
	 * @param relationModel
	 */
	public UndirectedRelationsTableModel(RelationModel relationModel) {
		this.relationModel = relationModel;
		this.relationModel.addRelationModelListener(this);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public int getRowCount() {
		if (this.relationModel == null)
			return 0;
		return this.relationModel.getRelations().select(r -> r instanceof SymmetricEntityRelation).size();
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
		EntityRelation er = this.relationModel.getRelations().select(r -> r instanceof SymmetricEntityRelation)
				.get(rowIndex);

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
		SymmetricEntityRelation erel = (SymmetricEntityRelation) this.relationModel.getRelations()
				.select(r -> r instanceof SymmetricEntityRelation).get(rowIndex);
		UpdateDirectedEntityRelation.EntityRelationProperty property = null;
		switch (columnIndex) {
		case 0:
			property = UpdateDirectedEntityRelation.EntityRelationProperty.TYPE;
			break;
		case 1:
			// property = UpdateUndirectedEntityRelation.EntityRelationProperty.ENTITIES;
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