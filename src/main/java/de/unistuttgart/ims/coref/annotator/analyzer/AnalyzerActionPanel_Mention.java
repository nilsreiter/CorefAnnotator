package de.unistuttgart.ims.coref.annotator.analyzer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.comp.SpringUtilities;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public class AnalyzerActionPanel_Mention extends AnalyzerActionPanel_ChartTable {

	private static final long serialVersionUID = 1L;

	Iterable<Entity> entities = null;

	public AnalyzerActionPanel_Mention(DocumentModel documentModel, Iterable<Entity> entities) {
		super(documentModel, entities);

		setEntities(entities);
	}

	@Override
	public AnalyzerActionPanel.ACTION getType() {
		return AnalyzerActionPanel.ACTION.MENTION;
	}

	@Override
	public void setEntities(Iterable<Entity> entities) {
		this.entities = entities;
		ImmutableList<Mention> mentions = Lists.immutable.withAll(entities)
				.flatCollect(e -> documentModel.getCoreferenceModel().getMentions(e));

		MutableMapIterable<String, Integer> cts = mentions.countBy(m -> m.getCoveredText()).toMapOfItemToCount();
		setFullData(cts);
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

		SpringUtilities.makeGrid(pan, 1, 2, // rows, cols
				0, 0, // initialX, initialY
				5, 5);// xPad, yPad

		return pan;
	}

}
