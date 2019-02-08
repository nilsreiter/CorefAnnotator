package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;

import javax.swing.JTable;

import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public class RelationTable extends JTable {

	private static final long serialVersionUID = 1L;

	public RelationTable(DocumentModel documentModel) {
		this.setModel(documentModel.getRelationModel().getTableModel());

		this.setGridColor(Color.GRAY);
		this.setAutoCreateColumnsFromModel(true);
		this.setAutoCreateRowSorter(true);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

	}

}
