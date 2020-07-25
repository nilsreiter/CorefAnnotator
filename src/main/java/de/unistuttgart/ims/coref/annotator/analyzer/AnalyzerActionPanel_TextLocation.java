package de.unistuttgart.ims.coref.annotator.analyzer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendLayout;
import org.knowm.xchart.style.Styler.LegendPosition;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.comp.EntityFromMentionTableCellRenderer;
import de.unistuttgart.ims.coref.annotator.comp.XChartPanel;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.uima.UimaUtil;

public class AnalyzerActionPanel_TextLocation extends AnalyzerActionPanel_GenericChartTable {
	public class EntityMouseListener extends MouseAdapter {
		Entity entity;
		XYSeries series;
		double yLevel;
		XYChart chart;

		public EntityMouseListener(XYChart chart, Entity entity, XYSeries series, double yLevel) {
			super();
			this.chart = chart;
			this.entity = entity;
			this.series = series;
			this.yLevel = yLevel;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			double x = chart.getChartXFromCoordinate(e.getX());
			double y = chart.getChartYFromCoordinate(e.getY());
			for (Mention m : documentModel.getCoreferenceModel().getMentions(entity)) {
				int begin = UimaUtil.getBegin(m) - 10;
				int end = UimaUtil.getEnd(m) + 10;
				if (begin <= x && end >= x /* && Math.abs(yLevel - y) < 0.1 */) {
					jtable.clearSelection();
					for (int r = 0; r < jtable.getRowCount(); r++) {
						Mention rowMention = (Mention) jtable.getValueAt(r, 0);
						if (rowMention == m) {
							jtable.setRowSelectionInterval(r, r);
							jtable.scrollRectToVisible(jtable.getCellRect(r, 0, false));
						}
					}
				}
			}

		}
	}

	public class MyTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;

		@Override
		public Class<?> getColumnClass(int c) {
			switch (c) {
			case 0:
				return Mention.class;
			case 1:
				return Integer.class;
			default:
				return String.class;
			}
		}

	}

	private static final long serialVersionUID = 1L;
	ListSelectionModel tableSelectionModel = new DefaultListSelectionModel();

	public AnalyzerActionPanel_TextLocation(DocumentModel documentModel, Iterable<Entity> entity) {
		super(documentModel, entity);

		init();

	}

	@Override
	protected String[] getColumnNames() {
		return new String[] { Annotator.getString(Strings.ANALYZER_ENTITY),
				Annotator.getString(Strings.ANALYZER_POSITION), Annotator.getString(Strings.ANALYZER_KWIC_LEFT),
				Annotator.getString(Strings.ANALYZER_KWIC_CENTER), Annotator.getString(Strings.ANALYZER_KWIC_RIGHT) };
	}

	@Override
	protected MyTableModel getTableModel() {
		return new MyTableModel();
	}

	@Override
	public AnalysisAction getType() {
		return AnalysisAction.TEXTLOCATION;
	}

	@Override
	protected void init() {
		super.init();
		jtable.setDefaultRenderer(Mention.class, new EntityFromMentionTableCellRenderer());
		jtable.setSelectionModel(tableSelectionModel);

		tableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	}

	@Override
	public void refresh() {
		chartPanelContainer.removeAll();

		XYChart chart = new XYChartBuilder().width(chartWidth).height(chartHeight).build();

		// Customize Chart
		chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
		chart.getStyler().setChartTitleVisible(false);
		chart.getStyler().setLegendPosition(LegendPosition.OutsideS);
		chart.getStyler().setLegendLayout(LegendLayout.Horizontal);
		chart.getStyler().setToolTipsEnabled(true);
		chart.getStyler().setChartBackgroundColor(getBackground());
		chart.getStyler().setToolTipsAlwaysVisible(false);
		chart.getStyler().setYAxisTitleVisible(false);
		chart.getStyler().setYAxisTicksVisible(false);
		chart.getStyler().setLegendVisible(false);
		chart.getStyler().setInfoPanelVisible(false);
		chart.setInfoContent(Lists.immutable.of("bla", "blubb").castToList());

		MutableList<Object[]> mentionLines = Lists.mutable.empty();
		MutableMap<Mention, Integer> mentionIndex = Maps.mutable.empty();
		int width = 50;

		// Series
		MutableList<MouseListener> listeners = Lists.mutable.empty();
		int y = 0;
		for (Entity e : entities) {
			Color entityColor = new Color(e.getColor());

			ImmutableSet<Mention> mentions = documentModel.getCoreferenceModel().getMentions(e);

			double[] xData = new double[mentions.size()];
			double[] yData = new double[mentions.size()];
			List<String> labels = new ArrayList<String>();
			int i = 0;
			for (Mention m : mentions) {
				int begin = UimaUtil.getBegin(m), end = UimaUtil.getEnd(m);
				xData[i] = begin;
				yData[i] = y;
				labels.add(UimaUtil.getCoveredText(m));

				String left = documentModel.getJcas().getDocumentText().substring(begin - width, begin);
				String center = UimaUtil.getCoveredText(m);
				String right = documentModel.getJcas().getDocumentText().substring(end, end + width);
				mentionLines.add(new Object[] { m, begin, left, center, right });
				mentionIndex.put(m, i);
				i++;
			}

			if (xData.length == 0)
				continue;

			XYSeries series = chart.addSeries(e.getLabel(), xData, yData);
			series.setToolTips(labels.toArray(new String[labels.size()]));
			series.setCustomToolTips(true);
			series.setFillColor(entityColor);
			listeners.add(new EntityMouseListener(chart, e, series, y));
			tableSelectionModel.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting())
						return;
					// chart.resetFilter();
					int row = e.getFirstIndex();
					Mention m = (Mention) jtable.getValueAt(row, 0);
					int seriesX = mentionIndex.get(m);
					// chart.filterXByIndex(seriesX - 10, seriesX + 10);
					// TODO: highlight data point or region in plot

					// repaint();
				}

			});
			y++;
		}

		tableModel.setDataVector(mentionLines.toArray(new Object[mentionLines.size()][]), this.getColumnNames());
		XChartPanel<XYChart> chartPanel = new XChartPanel<XYChart>(chart);
		chartPanel.setPreferredSize(new Dimension(chartWidth, chartHeight));
		listeners.forEach(ml -> chartPanel.addMouseListener(ml));

		chartPanelContainer.add(chartPanel, BorderLayout.CENTER);

		revalidate();

	}

}
