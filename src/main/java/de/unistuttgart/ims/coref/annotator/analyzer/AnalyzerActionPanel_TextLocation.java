package de.unistuttgart.ims.coref.annotator.analyzer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.knowm.xchart.XChartPanel;
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
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.uima.UimaUtil;

public class AnalyzerActionPanel_TextLocation extends AnalyzerActionPanel_GenericChartTable {
	public class MyTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;

		@Override
		public Class<?> getColumnClass(int c) {
			switch (c) {
			case 0:
				return Entity.class;
			default:
				return String.class;
			}
		}
	}

	private static final long serialVersionUID = 1L;

	public AnalyzerActionPanel_TextLocation(DocumentModel documentModel, Iterable<Entity> entity) {
		super(documentModel, entity);

		init();

	}

	@Override
	protected String[] getColumnNames() {
		return new String[] { Annotator.getString(Strings.ANALYZER_ENTITY),
				Annotator.getString(Strings.ANALYZER_KWIC_LEFT), Annotator.getString(Strings.ANALYZER_KWIC_CENTER),
				Annotator.getString(Strings.ANALYZER_KWIC_RIGHT) };
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
		chart.getStyler().setLegendVisible(false);

		MutableList<Object[]> mentionLines = Lists.mutable.empty();
		int width = 50;

		// Series

		int y = 0;
		for (Entity e : entities) {
			Color entityColor = new Color(e.getColor());

			List<Integer> xData = new ArrayList<Integer>();
			List<Integer> yData = new ArrayList<Integer>();
			List<String> labels = new ArrayList<String>();
			for (Mention m : documentModel.getCoreferenceModel().getMentions(e)) {
				int begin = UimaUtil.getBegin(m), end = UimaUtil.getEnd(m);
				xData.add(begin);
				yData.add(y);
				labels.add(UimaUtil.getCoveredText(m));

				String left = documentModel.getJcas().getDocumentText().substring(begin - width, begin);
				String center = UimaUtil.getCoveredText(m);
				String right = documentModel.getJcas().getDocumentText().substring(end, end + width);
				mentionLines.add(new Object[] { e, left, center, right });
			}

			if (xData.isEmpty())
				continue;
			XYSeries series = chart.addSeries(e.getLabel(), xData, yData);
			series.setToolTips(labels.toArray(new String[labels.size()]));
			series.setFillColor(entityColor);
			y++;
		}

		tableModel.setDataVector(mentionLines.toArray(new Object[mentionLines.size()][]), this.getColumnNames());

		XChartPanel<XYChart> chartPanel = new XChartPanel<XYChart>(chart);
		chartPanel.setPreferredSize(new Dimension(chartWidth, chartHeight));

		chartPanelContainer.add(chartPanel, BorderLayout.CENTER);

		revalidate();

	}

}
