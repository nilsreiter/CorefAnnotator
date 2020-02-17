package de.unistuttgart.ims.coref.annotator.analyzer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.comp.SpringUtilities;
import de.unistuttgart.ims.coref.annotator.comp.TranslatedListCellRenderer;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public class AnalyzerActionPanel_Mention extends AnalyzerActionPanel_ChartTable {

	private static final long serialVersionUID = 1L;

	GroupBy groupBy = GroupBy.COVEREDTEXT;

	public AnalyzerActionPanel_Mention(DocumentModel documentModel, Iterable<Entity> entities) {
		super(documentModel, entities);

		init();

	}

	@Override
	public AnalyzerActionPanel.ACTION getType() {
		return AnalyzerActionPanel.ACTION.MENTION;
	}

	@Override
	void calculateCounts() {
		ImmutableList<Mention> mentions = Lists.immutable.withAll(entities)
				.flatCollect(e -> documentModel.getCoreferenceModel().getMentions(e));

		cts = mentions.countBy(m -> groupBy.getFunction().apply(m)).toMapOfItemToCount();
	}

	@Override
	JPanel getOptionPanel() {
		JPanel pan = super.getOptionPanel();

		// unit
		pan.add(new JLabel(Annotator.getString(Strings.ANALYZER_GROUPBY)));

		DefaultComboBoxModel<GroupBy> unitBoxModel = new DefaultComboBoxModel<GroupBy>(GroupBy.values());

		JComboBox<GroupBy> unitBox = new JComboBox<GroupBy>(unitBoxModel);
		unitBox.setSelectedItem(groupBy);
		unitBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				groupBy = (GroupBy) unitBox.getSelectedItem();
				refresh();
			}

		});
		unitBox.setRenderer(new TranslatedListCellRenderer(Strings.ANALYZER_GROUPBY_));
		pan.add(unitBox);

		SpringUtilities.makeGrid(pan, pan.getComponents().length / 2, 2, 0, 0, 5, 5);

		return pan;
	}

}
