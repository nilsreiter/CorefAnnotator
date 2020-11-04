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
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.tuple.Tuples;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.CategorySeries;
import org.knowm.xchart.CategorySeries.CategorySeriesRenderStyle;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.PieSeries;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.internal.series.Series;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.colors.XChartSeriesColors;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import de.unistuttgart.ims.coref.annotator.comp.SpringUtilities;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public abstract class AnalyzerActionPanel_CountChartTable extends AnalyzerActionPanel_GenericChartTable {

	enum ChartType {
		PIE, BAR
	};

	private static final long serialVersionUID = 1L;

	MutableMapIterable<String, Integer> cts;

	double limit = 0.01;

	ChartType chartType = ChartType.BAR;

	public AnalyzerActionPanel_CountChartTable(DocumentModel documentModel, Iterable<Entity> entity) {
		super(documentModel, entity);
	}

	abstract void calculateCounts();

	int getTotalNumber() {
		if (this.cts == null)
			return 0;
		return (int) cts.valuesView().sumOfInt(i -> i);
	}

	@Override
	public void refresh() {
		chartPanelContainer.removeAll();

		calculateCounts();

		MutableMapIterable<String, Integer> entriesBelowThreshold = cts
				.select((s, i) -> (double) i / (double) getTotalNumber() < limit);

		MutableMapIterable<String, Integer> filteredCounts = cts
				.select((s, i) -> !entriesBelowThreshold.containsKey(s));

		if ((int) entriesBelowThreshold.valuesView().sumOfInt(i -> i) > 0)
			filteredCounts.put(Annotator.getString(Strings.ANALYZER_PLOT_REST_CATEGORY),
					(int) entriesBelowThreshold.valuesView().sumOfInt(i -> i));

		MutableMap<String, Series> seriesMap = Maps.mutable.empty();

		Color[] colors = new XChartSeriesColors().getSeriesColors();

		switch (chartType) {
		case BAR:
			CategoryChart categoryChart = new CategoryChartBuilder().width(chartWidth).height(chartHeight)
					.title(getClass().getSimpleName()).build();

			categoryChart.getStyler().setLegendPosition(LegendPosition.OutsideE);
			categoryChart.getStyler().setChartBackgroundColor(getBackground());
			categoryChart.getStyler().setChartTitleVisible(false);
			categoryChart.getStyler().setDefaultSeriesRenderStyle(CategorySeriesRenderStyle.Bar);
			categoryChart.getStyler().setLegendVisible(false);

			// Series
			int snum = 0;
			for (String c : filteredCounts.keySet()) {
				CategorySeries series = categoryChart.addSeries(c, new int[] { snum++ },
						new int[] { filteredCounts.get(c) });
				if (c.contentEquals(Annotator.getString(Strings.ANALYZER_PLOT_REST_CATEGORY))) {
					series.setFillColor(Color.lightGray);
				} else {
					series.setFillColor(colors[snum % colors.length]);
				}

				seriesMap.put(c, series);
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
			pieChart.getStyler().setLegendVisible(false);

			// Series
			snum = 0;
			for (String s : filteredCounts.keySet()) {
				PieSeries series = pieChart.addSeries(s, filteredCounts.get(s));
				if (s.contentEquals(Annotator.getString(Strings.ANALYZER_PLOT_REST_CATEGORY))) {
					series.setFillColor(Color.lightGray);
				} else {
					series.setFillColor(colors[snum++ % colors.length]);
				}
				seriesMap.put(s, series);

			}

			XChartPanel<PieChart> chartPanel;
			chartPanel = new XChartPanel<PieChart>(pieChart);
			chartPanel.setPreferredSize(new Dimension(400, 400));
			chartPanelContainer.add(chartPanel, BorderLayout.CENTER);
		}

		// TABLE
		Object[][] dv = cts
				.collect((s,
						i) -> Tuples.pair(new Object[] { (entriesBelowThreshold.containsKey(s) ? Color.lightGray
								: seriesMap.get(s).getFillColor()), s, i }, null))
				.keysView().toArray(new Object[cts.size()][]);
		tableModel.setDataVector(dv, this.getColumnNames());

		revalidate();
	}

	@Override
	protected MyTableModel getTableModel() {
		return new MyTableModel();
	}

	@Override
	JPanel getOptionPanel() {
		JPanel pan = new JPanel();
		pan.setLayout(new SpringLayout());

		JLabel lab = new JLabel(Annotator.getString(Strings.ANALYZER_PLOT_REST_LIMIT));
		pan.add(lab);

		JSpinner spinner = new JSpinner(new SpinnerNumberModel(limit, 0, 1, 0.01));
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
		plotTypeBox.setSelectedItem(chartType);
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

	public class MyTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;

		@Override
		public Class<?> getColumnClass(int c) {
			switch (c) {
			case 0:
				return Color.class;
			case 1:
				return String.class;
			default:
				return Integer.class;
			}
		}
	}

	@Override
	protected String[] getColumnNames() {
		return new String[] { Annotator.getString(Strings.ANALYZER_COLOR),
				Annotator.getString(Strings.ANALYZER_DATATABLE_MENTIONS),
				Annotator.getString(Strings.ANALYZER_DATATABLE_COUNT) };
	}

}
