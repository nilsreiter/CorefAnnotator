package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.action.AddFlagAction;
import de.unistuttgart.ims.coref.annotator.action.DeleteFlagAction;
import de.unistuttgart.ims.coref.annotator.api.v1.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.v1.DirectedEntityRelation;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.comp.DefaultTableHeaderCellRenderer;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.adapter.FlagTableModel;

public class FlagEditor extends JFrame {

	private static final long serialVersionUID = 1L;

	DocumentModel documentModel;
	DocumentWindow documentWindow;

	JTable table;
	JPanel toolbar;

	public FlagEditor(DocumentModel documentModel, DocumentWindow documentWindow) {
		this.documentModel = documentModel;
		this.setTitle(Annotator.getString(Constants.Strings.FLAG_EDITOR) + ": " + documentWindow.getTitle());
		this.addWindowListener(new FlagEditorWindowListener());

		JComboBox<Class<?>> combobox = new JComboBox<Class<?>>();
		combobox.addItem(Mention.class);
		combobox.addItem(Entity.class);
		combobox.addItem(DetachedMentionPart.class);
		combobox.addItem(DirectedEntityRelation.class);
		combobox.setRenderer(new TargetClassListCellRenderer());

		JComboBox<Ikon> iconBox = new JComboBox<Ikon>();
		for (MaterialDesign mdi : MaterialDesign.values()) {
			iconBox.addItem(mdi);
		}
		iconBox.setRenderer(new IkonListCellRenderer());

		this.table = new JTable(new FlagTableModel(documentModel));

		// Actions
		AbstractAction addFlagAction = new AddFlagAction(documentModel);
		DeleteFlagAction deleteFlagAction = new DeleteFlagAction(documentModel, table);

		// Table
		this.table.setGridColor(Color.GRAY);
		this.table.setAutoCreateColumnsFromModel(true);
		this.table.setAutoCreateRowSorter(true);
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		this.table.setDefaultRenderer(Ikon.class, new IkonTableCellRenderer());
		this.table.setDefaultRenderer(Class.class, new TargetClassTableCellRenderer());
		this.table.setDefaultEditor(Class.class, new DefaultCellEditor(combobox));
		this.table.setDefaultEditor(Ikon.class, new DefaultCellEditor(iconBox));
		this.table.setCellEditor(new DefaultCellEditor(new JTextField()));
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.getSelectionModel().addListSelectionListener(deleteFlagAction);
		this.table.setRowHeight(25);

		this.table.getColumnModel().getColumn(0).setHeaderRenderer(new DefaultTableHeaderCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getToolTipText() {
				return Annotator.getString(Constants.Strings.FLAG_EDITOR_ICON_TOOLTIP);
			}
		});
		this.table.getColumnModel().getColumn(1).setHeaderRenderer(new DefaultTableHeaderCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getToolTipText() {
				return Annotator.getString(Constants.Strings.FLAG_EDITOR_KEY_TOOLTIP);
			}
		});
		this.table.getColumnModel().getColumn(2).setHeaderRenderer(new DefaultTableHeaderCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getToolTipText() {
				return Annotator.getString(Constants.Strings.FLAG_EDITOR_LABEL_TOOLTIP);
			}
		});
		this.table.getColumnModel().getColumn(3).setHeaderRenderer(new DefaultTableHeaderCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getToolTipText() {
				return Annotator.getString(Constants.Strings.FLAG_EDITOR_TARGETCLASS_TOOLTIP);
			}
		});

		this.toolbar = new JPanel();
		this.toolbar.add(new JButton(addFlagAction));
		this.toolbar.add(new JButton(deleteFlagAction));

		this.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
		this.getContentPane().add(toolbar, BorderLayout.NORTH);
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

	class MyTableHeader extends JTableHeader {

		private static final long serialVersionUID = 1L;
		String[] tooltips;

		MyTableHeader(TableColumnModel columnModel, String[] columnTooltips) {
			super(columnModel);// do everything a normal JTableHeader does
			this.tooltips = columnTooltips;// plus extra data
		}

		@Override
		public String getToolTipText(MouseEvent e) {
			java.awt.Point p = e.getPoint();
			int index = columnModel.getColumnIndexAtX(p.x);
			int realIndex = columnModel.getColumn(index).getModelIndex();
			return this.tooltips[realIndex];
		}
	}

	class FlagEditorWindowListener implements WindowListener {

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosing(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosed(WindowEvent e) {

		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class MyHeaderRenderer extends DefaultTableHeaderCellRenderer {

		private static final long serialVersionUID = 1L;

	}

}
