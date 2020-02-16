package de.unistuttgart.ims.coref.annotator.analyzer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.impl.tuple.Tuples;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.CategorySeries;
import org.knowm.xchart.CategorySeries.CategorySeriesRenderStyle;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.PieSeries;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.comp.SpringUtilities;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public abstract class AnalyzerActionPanel_ChartTable extends AnalyzerActionPanel {

	enum ChartType {
		PIE, BAR
	};

	private static final long serialVersionUID = 1L;

	MutableMapIterable<String, Integer> cts;

	double limit = 0.04;

	JTable jtable = new JTable();
	MyTableModel tableModel = new MyTableModel();
	JScrollPane tableScroller = new JScrollPane(jtable);
	JPanel chartPanelContainer = new JPanel();
	ChartType chartType = ChartType.BAR;

	public AnalyzerActionPanel_ChartTable(DocumentModel documentModel, Iterable<Entity> entity) {
		super(documentModel, entity);

	}

	@Override
	void init() {
		super.init();

		chartPanelContainer.setLayout(new BorderLayout());
		add(chartPanelContainer);
		chartConstraints(chartPanelContainer);

		jtable.setAutoCreateRowSorter(true);
		jtable.setModel(tableModel);
		jtable.setShowGrid(true);
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
		MutableMapIterable<String, Integer> smallest = cts
				.select((s, i) -> (double) i / (double) getTotalNumber() < limit);

		MutableMapIterable<String, Integer> cts2 = cts.select((s, i) -> !smallest.containsKey(s));
		cts2.put(Annotator.getString(Strings.ANALYZER_PLOT_REST_CATEGORY),
				(int) smallest.valuesView().sumOfInt(i -> i));

		switch (chartType) {
		case BAR:
			CategoryChart categoryChart = new CategoryChartBuilder().width(chartWidth).height(chartHeight)
					.title(getClass().getSimpleName()).build();

			categoryChart.getStyler().setLegendPosition(LegendPosition.OutsideE);
			categoryChart.getStyler().setChartBackgroundColor(getBackground());
			categoryChart.getStyler().setChartTitleVisible(false);
			categoryChart.getStyler().setDefaultSeriesRenderStyle(CategorySeriesRenderStyle.Bar);

			// Series
			int snum = 0;
			for (String c : cts2.keySet()) {
				CategorySeries series = categoryChart.addSeries(c, new int[] { snum++ }, new int[] { cts2.get(c) });
				if (c.contentEquals(Annotator.getString(Strings.ANALYZER_PLOT_REST_CATEGORY))) {
					series.setFillColor(Color.lightGray);
				}
			}
			XChartPanel<CategoryChart> categoryChartPanel;
			categoryChartPanel = new XChartPanel<CategoryChart>(categoryChart);
			categoryChartPanel.setPreferredSize(new Dimension(400, 400));
			chartPanelContainer.add(categoryChartPanel, BorderLayout.CENTER);

			break;
		default:
			// CHART
			PieChart pieChart = new PieChartBuilder().width(chartWidth).height(chartHeight)
					.title(getClass().getSimpleName()).build();

			// Customize Chart
			pieChart.getStyler().setLegendPosition(LegendPosition.OutsideE);
			pieChart.getStyler().setChartBackgroundColor(getBackground());
			pieChart.getStyler().setChartTitleVisible(false);

			// Series

			cts2.forEach((s, i) -> {
				PieSeries series = pieChart.addSeries(s, i);
				if (s.contentEquals(Annotator.getString(Strings.ANALYZER_PLOT_REST_CATEGORY))) {
					series.setFillColor(Color.lightGray);
				}
			});

			XChartPanel<PieChart> chartPanel;
			chartPanel = new XChartPanel<PieChart>(pieChart);
			chartPanel.setPreferredSize(new Dimension(400, 400));
			chartPanelContainer.add(chartPanel, BorderLayout.CENTER);
		}

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

	@Override
	JPanel getOptionPanel() {
		JPanel pan = new JPanel();
		pan.setLayout(new SpringLayout());

		JLabel lab = new JLabel(Annotator.getString(Strings.ANALYZER_PLOT_REST_LIMIT));
		pan.add(lab);

		JSpinner spinner = new JSpinner(new SpinnerNumberModel(limit, 0, 1, 0.02));
		spinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				limit = (double) spinner.getValue();
				refresh();
			}

		});
		pan.add(spinner);

		pan.add(new JLabel(Annotator.getString(Strings.ANALYZER_PLOT_PLOT_TYPE)));

		JComboBox<ChartType> plotTypeBox = new JComboBox<ChartType>(ChartType.values());
		plotTypeBox.setSelectedItem(ChartType.BAR);
		plotTypeBox.setRenderer(new DefaultListCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {

				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value != null)
					switch ((ChartType) value) {
					case BAR:
						setIcon(FontIcon.of(MaterialDesign.MDI_CHART_BAR));
						setText(Annotator.getString(Strings.ANALYZER_PLOT_PLOT_TYPE_BAR));
						break;
					default:
						setIcon(FontIcon.of(MaterialDesign.MDI_CHART_PIE));
						setText(Annotator.getString(Strings.ANALYZER_PLOT_PLOT_TYPE_PIE));
					}

				return this;
			}

		});
		plotTypeBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chartType = (ChartType) plotTypeBox.getSelectedItem();
				refresh();
			}

		});
		pan.add(plotTypeBox);

		SpringUtilities.makeGrid(pan, 2, 2, // rows, cols
				0, 0, // initialX, initialY
				5, 5);// xPad, yPad

		return pan;
	}

}
