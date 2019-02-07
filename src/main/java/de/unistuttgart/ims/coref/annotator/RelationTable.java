package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityRelationType;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.Event;
import de.unistuttgart.ims.coref.annotator.document.RelationModelListener;

public class RelationTable extends JTable {

	private static final long serialVersionUID = 1L;

	public RelationTable(DocumentModel documentModel) {
		this.setModel(new RelationTableModel(documentModel));

		this.setGridColor(Color.GRAY);
		this.setAutoCreateColumnsFromModel(true);
		this.setAutoCreateRowSorter(true);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

	}

	class RelationTableModel implements TableModel, RelationModelListener {
		DocumentModel documentModel;

		public RelationTableModel(DocumentModel documentModel) {
			this.documentModel = documentModel;
		}

		@Override
		public int getRowCount() {
			return documentModel.getRelationModel().getRelations().size();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public String getColumnName(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return "Entity 1";
			case 1:
				return "Entity 2";
			case 2:
				return "Relation";
			}
			return null;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
			case 1:
				return Entity.class;
			case 2:
				return EntityRelationType.class;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub

		}

		@Override
		public void addTableModelListener(TableModelListener l) {
			// TODO Auto-generated method stub

		}

		@Override
		public void removeTableModelListener(TableModelListener l) {
			// TODO Auto-generated method stub

		}

		@Override
		public void relationEvent(Event event) {
			switch (event.getType()) {
			case Add:
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
				break;
			default:
				break;

			}
		}

	}

}
