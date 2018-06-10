package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

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
		combobox.setRenderer(new TargetClassListCellRenderer());

		JComboBox<Ikon> iconBox = new JComboBox<Ikon>();
		for (MaterialDesign mdi : MaterialDesign.values()) {
			iconBox.addItem(mdi);
		}
		iconBox.setRenderer(new IkonListCellRenderer());

		this.table = new JTable(new FlagTableModel(documentModel.getFlagModel()));
		this.table.setGridColor(Color.GRAY);
		this.table.setAutoCreateColumnsFromModel(true);
		this.table.setAutoCreateRowSorter(true);
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		this.table.setDefaultRenderer(Ikon.class, new IkonTableCellRenderer());
		this.table.setDefaultRenderer(Class.class, new TargetClassTableCellRenderer());
		this.table.setDefaultEditor(Class.class, new DefaultCellEditor(combobox));
		this.table.setDefaultEditor(Ikon.class, new DefaultCellEditor(iconBox));
		this.table.setCellEditor(new DefaultCellEditor(new JTextField()));

		this.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		this.setVisible(true);
		this.pack();
	}

	class TargetClassListCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value != null)
				setText(((Class<?>) value).getSimpleName());

			return this;
		}
	}

	class TargetClassTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value != null)
				setText(((Class<?>) value).getSimpleName());

			return this;
		}

	}

	class IkonTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value != null)
				setIcon(FontIcon.of((Ikon) value));

			return this;
		}
	}

	class IkonListCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value != null)
				setIcon(FontIcon.of((Ikon) value));

			return this;
		}

	}

}
