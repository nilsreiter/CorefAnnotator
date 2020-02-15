package de.unistuttgart.ims.coref.annotator.analyzer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendLayout;
import org.knowm.xchart.style.Styler.LegendPosition;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public class AnalyzerActionPanel_TextLocation extends AnalyzerActionPanel {

	private static final long serialVersionUID = 1L;
	Iterable<Entity> entities;

	public AnalyzerActionPanel_TextLocation(DocumentModel documentModel, Iterable<Entity> entity) {
		super(documentModel, entity);
		setEntities(entity);
	}

	@Override
	public ACTION getType() {
		return ACTION.TEXTLOCATION;
	}

	@Override
	public void setEntities(Iterable<Entity> entities) {
		removeAll();

		this.entities = entities;

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

		// Series
		int y = 0;
		for (Entity e : entities) {
			List<Integer> xData = new ArrayList<Integer>();
			List<Integer> yData = new ArrayList<Integer>();
			List<String> labels = new ArrayList<String>();
			for (Mention m : documentModel.getCoreferenceModel().getMentions(e)) {
				xData.add(m.getBegin());
				yData.add(y);
				labels.add(m.getCoveredText());
			}

			if (xData.isEmpty())
				continue;
			XYSeries series = chart.addSeries(e.getLabel(), xData, yData);
			series.setToolTips(labels.toArray(new String[labels.size()]));
			series.setFillColor(new Color(e.getColor()));
			y++;
		}

		// JList<Mention> exampleSentences = new JList<Mention>();
		// DefaultListModel<Mention> listModel = new DefaultListModel<Mention>();
		// exampleSentences.setModel(listModel);
		/*
		 * exampleSentences.setCellRenderer(new DefaultListCellRenderer() {
		 * 
		 * private static final long serialVersionUID = 1L;
		 * 
		 * @Override public Component getListCellRendererComponent(JList<?> list, Object
		 * value, int index, boolean isSelected, boolean cellHasFocus) { return
		 * super.getListCellRendererComponent(list, ((Annotation)
		 * value).getCoveredText(), index, isSelected, cellHasFocus); }
		 * 
		 * });
		 */

		XChartPanel<XYChart> chartPanel = new XChartPanel<XYChart>(chart);
		/*
		 * chartPanel.addMouseListener(new MouseListener() {
		 * 
		 * @Override public void mouseClicked(MouseEvent e) { listModel.clear(); int x =
		 * (int) Math.ceil(chart.getChartXFromCoordinate(e.getX()));
		 * MutableSet<Annotation> ms = documentModel.getCoreferenceModel().getMentions(x
		 * + 1); for (Annotation a : ms) { listModel.addElement((Mention) a); } }
		 * 
		 * @Override public void mousePressed(MouseEvent e) {
		 * 
		 * }
		 * 
		 * @Override public void mouseReleased(MouseEvent e) {
		 * 
		 * }
		 * 
		 * @Override public void mouseEntered(MouseEvent e) { }
		 * 
		 * @Override public void mouseExited(MouseEvent e) {
		 * 
		 * }
		 * 
		 * });
		 */
		add(chartPanel);
		chartConstraints(chartPanel);

		// add(exampleSentences);
		// layout.putConstraint(SpringLayout.SOUTH, exampleSentences, -2 * gap,
		// SpringLayout.SOUTH, this);

		revalidate();

	}

}
