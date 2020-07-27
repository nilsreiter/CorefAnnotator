package de.unistuttgart.ims.coref.annotator.analyzer;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;

import de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import de.unistuttgart.ims.coref.annotator.comp.ColorTableCellRenderer;
import de.unistuttgart.ims.coref.annotator.comp.EntityTableCellRenderer;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public abstract class AnalyzerActionPanel_GenericChartTable extends AnalyzerActionPanel {

	private static final long serialVersionUID = 1L;
	JTable jtable = new JTable();
	DefaultTableModel tableModel;
	JScrollPane tableScroller = new JScrollPane(jtable);
	protected JPanel chartPanelContainer = new JPanel();

	public AnalyzerActionPanel_GenericChartTable(DocumentModel documentModel, Iterable<Entity> entity) {
		super(documentModel, entity);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void init() {
		super.init();

		chartPanelContainer.setLayout(new BorderLayout());
		add(chartPanelContainer);
		chartConstraints(chartPanelContainer);

		tableModel = getTableModel();
		jtable.setAutoCreateRowSorter(true);
		jtable.setModel(tableModel);
		jtable.setShowGrid(true);
		jtable.setDefaultRenderer(Color.class, new ColorTableCellRenderer());
		jtable.setDefaultRenderer(Entity.class, new EntityTableCellRenderer());
		add(tableScroller);

		layout.putConstraint(SpringLayout.WEST, tableScroller, gap, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, this, gap, SpringLayout.EAST, tableScroller);
		layout.putConstraint(SpringLayout.SOUTH, this, gap, SpringLayout.SOUTH, tableScroller);
		layout.putConstraint(SpringLayout.NORTH, tableScroller, gap, SpringLayout.SOUTH, chartPanelContainer);
	}

	protected abstract DefaultTableModel getTableModel();

	protected abstract String[] getColumnNames();

}
