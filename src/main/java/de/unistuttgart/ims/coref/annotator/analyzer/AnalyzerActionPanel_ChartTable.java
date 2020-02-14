package de.unistuttgart.ims.coref.annotator.analyzer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;

import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.impl.tuple.Tuples;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler.LegendPosition;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public abstract class AnalyzerActionPanel_ChartTable extends AnalyzerActionPanel {

	private static final long serialVersionUID = 1L;

	MutableMapIterable<String, Integer> cts;

	double limit = 0.04;

	JTable jtable;
	MyTableModel tableModel;
	JScrollPane tableScroller;
	JPanel chartPanelContainer;

	public AnalyzerActionPanel_ChartTable(DocumentModel documentModel, Iterable<Entity> entity) {
		super(documentModel, entity);

		chartPanelContainer = new JPanel();
		chartPanelContainer.setLayout(new BorderLayout());
		add(chartPanelContainer);
		chartConstraints(chartPanelContainer);

		tableModel = new MyTableModel();
		jtable = new JTable();
		jtable.setAutoCreateRowSorter(true);
		jtable.setModel(tableModel);
		jtable.setShowGrid(true);
		tableScroller = new JScrollPane(jtable);
		add(tableScroller);

		layout.putConstraint(SpringLayout.WEST, tableScroller, gap, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, this, gap, SpringLayout.EAST, tableScroller);
		layout.putConstraint(SpringLayout.SOUTH, this, gap, SpringLayout.SOUTH, tableScroller);
		layout.putConstraint(SpringLayout.NORTH, tableScroller, gap, SpringLayout.SOUTH, chartPanelContainer);

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
		chartPanelContainer.removeAll();

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
		cts2.put(Annotator.getString(Strings.ANALYZER_PLOT_REST_CATEGORY),
				(int) smallest.valuesView().sumOfInt(i -> i));

		cts2.forEach((s, i) -> {
			chart.addSeries(s, i);
		});

		XChartPanel<PieChart> chartPanel;
		chartPanel = new XChartPanel<PieChart>(chart);
		chartPanel.setPreferredSize(new Dimension(400, 400));
		chartPanelContainer.add(chartPanel, BorderLayout.CENTER);

		// TABLE
		Object[][] dv = cts.collect((s, i) -> Tuples.pair(new Object[] { s, i }, null)).keysView()
				.toArray(new Object[cts.size()][]);
		tableModel.setDataVector(dv, new String[] { Annotator.getString(Strings.ANALYZER_DATATABLE_MENTIONS),
				Annotator.getString(Strings.ANALYZER_DATATABLE_COUNT) });

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
