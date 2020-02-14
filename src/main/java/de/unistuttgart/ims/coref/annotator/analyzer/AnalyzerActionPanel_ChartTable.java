package de.unistuttgart.ims.coref.annotator.analyzer;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.impl.tuple.Tuples;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler.LegendPosition;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public abstract class AnalyzerActionPanel_ChartTable extends AnalyzerActionPanel {

	private static final long serialVersionUID = 1L;

	MutableMapIterable<String, Integer> cts;

	double limit = 0.04;

	public AnalyzerActionPanel_ChartTable(DocumentModel documentModel, Iterable<Entity> entity) {
		super(documentModel, entity);
	}

	void setFullData(MutableMapIterable<String, Integer> counts) {
		this.cts = counts;
		refresh();
	}

	int getTotalNumber() {
		if (this.cts == null)
			return 0;
		return (int) cts.valuesView().sumOfInt(i -> i);
	}

	public void refresh() {
		removeAll();

		// CHART
		PieChart chart = new PieChartBuilder().width(400).height(400).title(getClass().getSimpleName()).build();

		// Customize Chart
		Color[] sliceColors = new Color[] { new Color(224, 68, 14), new Color(230, 105, 62), new Color(236, 143, 110),
				new Color(243, 180, 159), new Color(246, 199, 182) };
		chart.getStyler().setSeriesColors(sliceColors);
		chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
		chart.getStyler().setChartBackgroundColor(getBackground());
		chart.getStyler().setChartTitleVisible(false);

		// Series
		MutableMapIterable<String, Integer> smallest = cts
				.select((s, i) -> (double) i / (double) getTotalNumber() < limit);

		MutableMapIterable<String, Integer> cts2 = cts.select((s, i) -> !smallest.containsKey(s));
		cts2.put("Rest", (int) smallest.valuesView().sumOfInt(i -> i));

		cts2.forEach((s, i) -> {
			chart.addSeries(s, i);
		});

		XChartPanel<PieChart> chartPanel = new XChartPanel<PieChart>(chart);
		chartPanel.setPreferredSize(new Dimension(400, 400));
		add(chartPanel);
		chartConstraints(chartPanel);

		// OPTIONS
		JLabel lab = new JLabel("Group below");
		add(lab);
		layout.putConstraint(SpringLayout.NORTH, lab, gap, SpringLayout.SOUTH, chartPanel);
		layout.putConstraint(SpringLayout.WEST, lab, gap, SpringLayout.WEST, this);

		JSpinner spinner = new JSpinner(new SpinnerNumberModel(limit, 0, 1, 0.02));
		spinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				limit = (double) spinner.getValue();
				refresh();
			}

		});
		add(spinner);
		layout.putConstraint(SpringLayout.NORTH, spinner, gap, SpringLayout.SOUTH, chartPanel);
		layout.putConstraint(SpringLayout.EAST, spinner, -gap, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.WEST, spinner, Spring.constant(5, 20, 300), SpringLayout.EAST, lab);

		// TABLE
		JTable jtable = new JTable();
		MyTableModel tm = new MyTableModel();
		Object[][] dv = cts.collect((s, i) -> Tuples.pair(new Object[] { s, i }, null)).keysView()
				.toArray(new Object[cts.size()][]);
		tm.setDataVector(dv, new String[] { "Mention", "Number" });
		// TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tm);

		jtable.setAutoCreateRowSorter(true);
		jtable.setModel(tm);
		jtable.setShowGrid(true);
		// jtable.setRowSorter(sorter);

		JScrollPane tableScroller = new JScrollPane(jtable);
		add(tableScroller);

		layout.putConstraint(SpringLayout.NORTH, tableScroller, gap, SpringLayout.SOUTH, spinner);
		layout.putConstraint(SpringLayout.WEST, tableScroller, gap, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, this, gap, SpringLayout.EAST, tableScroller);
		layout.putConstraint(SpringLayout.SOUTH, this, gap, SpringLayout.SOUTH, tableScroller);

		revalidate();
	}

	public class MyTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;

		@Override
		public Class<?> getColumnClass(int c) {
			if (c == 0)
				return String.class;
			else
				return Integer.class;
		}
	}

}