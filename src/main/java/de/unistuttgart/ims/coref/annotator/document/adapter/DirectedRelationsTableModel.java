package de.unistuttgart.ims.coref.annotator.document.adapter;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.eclipse.collections.api.list.ImmutableList;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.NodeListTransferable;
import de.unistuttgart.ims.coref.annotator.api.v1.DirectedEntityRelation;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityRelation;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;
import de.unistuttgart.ims.coref.annotator.document.RelationModel;
import de.unistuttgart.ims.coref.annotator.document.RelationModelListener;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateEntityRelation;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateEntityRelation.EntityRelationProperty;

public class DirectedRelationsTableModel extends DefaultTableModel implements TableModel, RelationModelListener {

	private RelationModel relationModel;

	/**
	 * @param relationModel
	 */
	public DirectedRelationsTableModel(RelationModel relationModel) {
		this.relationModel = relationModel;
		this.relationModel.addRelationModelListener(this);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public int getRowCount() {
		if (this.relationModel == null)
			return 0;
		return this.relationModel.getRelations().select(r -> r instanceof DirectedEntityRelation).size();
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
		EntityRelation er = this.relationModel.getRelations().select(r -> r instanceof DirectedEntityRelation)
				.get(rowIndex);

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
		DirectedEntityRelation erel = (DirectedEntityRelation) this.relationModel.getRelations()
				.select(r -> r instanceof DirectedEntityRelation).get(rowIndex);
		UpdateEntityRelation.EntityRelationProperty property = null;
		switch (columnIndex) {
		case 0:
			property = UpdateEntityRelation.EntityRelationProperty.SOURCE;
			break;
		case 1:
			property = UpdateEntityRelation.EntityRelationProperty.TARGET;
			break;
		case 2:
			property = UpdateEntityRelation.EntityRelationProperty.TYPE;
			break;
		}
		this.relationModel.getDocumentModel().edit(new UpdateEntityRelation(erel, property, aValue));
	}

	@Override
	public void relationEvent(FeatureStructureEvent event) {
		TableModelEvent tme = null;

		if (!(event.getArgument1() instanceof DirectedEntityRelation))
			return;
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

	public javax.swing.TransferHandler getTransferHandler() {
		return new TransferHandler();
	}

	class TransferHandler extends javax.swing.TransferHandler {

		private static final long serialVersionUID = 1L;

		@Override
		public boolean canImport(TransferHandler.TransferSupport info) {
			if (!info.isDataFlavorSupported(NodeListTransferable.dataFlavor)) {
				return false;
			}
			JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
			if (dl.getColumn() != 0 && dl.getColumn() != 1)
				return false;
			return true;
		}

		@Override
		public boolean importData(TransferHandler.TransferSupport info) {
			if (!info.isDrop()) {
				return false;
			}

			// Check for flavor
			if (!info.isDataFlavorSupported(NodeListTransferable.dataFlavor)) {
				return false;
			}

			JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
			DirectedEntityRelation erel = (DirectedEntityRelation) relationModel.getRelations()
					.select(r -> r instanceof DirectedEntityRelation).get(dl.getRow());
			UpdateEntityRelation.EntityRelationProperty property = null;
			switch (dl.getColumn()) {
			case 0:
				property = EntityRelationProperty.SOURCE;
				break;
			case 1:
				property = EntityRelationProperty.TARGET;
				break;
			case 2:
				return false;
			}
			try {
				@SuppressWarnings("unchecked")
				ImmutableList<CATreeNode> treeNodes = (ImmutableList<CATreeNode>) info.getTransferable()
						.getTransferData(NodeListTransferable.dataFlavor);
				relationModel.getDocumentModel().edit(
						new UpdateEntityRelation(erel, property, treeNodes.collect(tn -> tn.getEntity()).getFirst()));
				return true;
			} catch (UnsupportedFlavorException | IOException e) {
				e.printStackTrace();
			}

			return false;
		}
	}
}