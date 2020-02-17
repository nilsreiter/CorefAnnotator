package de.unistuttgart.ims.coref.annotator.analyzer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.comp.SpringUtilities;
import de.unistuttgart.ims.coref.annotator.comp.TranslatedListCellRenderer;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public class AnalyzerActionPanel_Flag extends AnalyzerActionPanel_ChartTable {

	private static final long serialVersionUID = 1L;

	int n = 1;
	Class<? extends TOP> unit = Mention.class;

	public AnalyzerActionPanel_Flag(DocumentModel documentModel, Iterable<Entity> entity) {
		super(documentModel, entity);

		init();

		refresh();
	}

	@Override
	JPanel getOptionPanel() {
		JPanel pan = super.getOptionPanel();

		// unit
		pan.add(new JLabel(Annotator.getString(Strings.ANALYZER_NEIGHBOUR_UNIT)));

		DefaultComboBoxModel<Class<?>> unitBoxModel = new DefaultComboBoxModel<Class<?>>();
		unitBoxModel.addElement(Mention.class);
		unitBoxModel.addElement(Entity.class);

		JComboBox<Class<?>> unitBox = new JComboBox<Class<?>>(unitBoxModel);
		unitBox.setSelectedItem(unit);
		unitBox.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				unit = (Class<? extends Annotation>) unitBox.getSelectedItem();
				refresh();
			}

		});
		unitBox.setRenderer(new TranslatedListCellRenderer(Strings.ANALYZER_NEIGHBOUR_UNIT_) {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
				return super.getListCellRendererComponent(list, ((Class<?>) value).getSimpleName(), index, isSelected,
						cellHasFocus);

			}
		});
		pan.add(unitBox);

		SpringUtilities.makeGrid(pan, pan.getComponents().length / 2, 2, // rows, cols
				0, 0, // initialX, initialY
				5, 5);// xPad, yPad
		return pan;
	}

	@Override
	public ACTION getType() {
		return ACTION.Flag;
	}

	@Override
	void calculateCounts() {
		cts = Maps.mutable.empty();
		if (unit == Entity.class) {
			for (Flag f : documentModel.getFlagModel().getFlags()
					.select(f -> f.getTargetClass().equalsIgnoreCase(Entity.class.getName()))) {
				cts.put(f.getLabel(), Lists.mutable.withAll(entities).select(e -> Util.isX(e, f.getKey())).size());
			}
		} else if (unit == Mention.class) {
			MutableList<Mention> mentions = Lists.mutable.withAll(entities)
					.flatCollect(e -> documentModel.getCoreferenceModel().getMentions(e));
			for (Flag f : documentModel.getFlagModel().getFlags()
					.select(f -> f.getTargetClass().equalsIgnoreCase(Mention.class.getName()))) {
				cts.put(f.getLabel(), mentions.select(e -> Util.isX(e, f.getKey())).size());
			}
		}

	}

}
