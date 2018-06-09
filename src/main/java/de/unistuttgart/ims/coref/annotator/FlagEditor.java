package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;

import de.unistuttgart.ims.coref.annotator.api.v1.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.FlagTableModel;

public class FlagEditor extends JFrame {

	private static final long serialVersionUID = 1L;

	DocumentModel documentModel;

	JTable table;

	public FlagEditor(DocumentModel documentModel) {
		this.documentModel = documentModel;

		JComboBox<Class<?>> combobox = new JComboBox<Class<?>>();
		combobox.addItem(Mention.class);
		combobox.addItem(Entity.class);
		combobox.addItem(DetachedMentionPart.class);

		this.table = new JTable(new FlagTableModel(documentModel.getFlagModel()));
		this.table.setCellEditor(new CellEditor());
		this.table.setGridColor(Color.GRAY);
		this.table.setTableHeader(new JTableHeader());
		this.table.setAutoCreateColumnsFromModel(true);
		this.table.setAutoCreateRowSorter(true);
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		this.table.setDefaultEditor(Class.class, new DefaultCellEditor(combobox));

		this.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		this.setVisible(true);
		this.pack();
	}

	class TargetClassEditor extends DefaultCellEditor {

		private static final long serialVersionUID = 1L;

		public TargetClassEditor(JComboBox<Class<?>> comboBox) {
			super(comboBox);
		}

	}

	class CellEditor extends DefaultCellEditor {

		private static final long serialVersionUID = 1L;

		public CellEditor() {
			super(new JTextField());
		}

	}

}
