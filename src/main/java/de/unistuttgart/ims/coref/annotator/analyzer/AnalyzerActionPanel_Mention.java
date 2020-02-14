package de.unistuttgart.ims.coref.annotator.analyzer;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.impl.factory.Lists;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler.LegendPosition;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public class AnalyzerActionPanel_Mention extends AnalyzerActionPanel {

	private static final long serialVersionUID = 1L;

	Iterable<Entity> entities = null;

	double limit = 0.04;

	public AnalyzerActionPanel_Mention(DocumentModel documentModel, Iterable<Entity> entities) {
		super(documentModel, entities);

		setPreferredSize(new Dimension(400, 400));

		setEntities(entities);
	}

	@Override
	public AnalyzerActionPanel.ACTION getType() {
		return AnalyzerActionPanel.ACTION.MENTION;
	}

	public void refresh() {
		removeAll();

		ImmutableList<Mention> mentions = Lists.immutable.withAll(entities)
				.flatCollect(e -> documentModel.getCoreferenceModel().getMentions(e));

		MutableMapIterable<String, Integer> cts = mentions.countBy(m -> m.getCoveredText()).toMapOfItemToCount();

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
				.select((s, i) -> (double) i / (double) mentions.size() < limit);

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
		revalidate();
	}

	@Override
	public void setEntities(Iterable<Entity> entities) {
		this.entities = entities;
		refresh();
	}

}
