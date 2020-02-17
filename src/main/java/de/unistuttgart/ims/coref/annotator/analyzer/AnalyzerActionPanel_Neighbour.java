package de.unistuttgart.ims.coref.annotator.analyzer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.comp.SpringUtilities;
import de.unistuttgart.ims.coref.annotator.comp.TranslatedListCellRenderer;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public class AnalyzerActionPanel_Neighbour extends AnalyzerActionPanel_ChartTable {

	private static final long serialVersionUID = 1L;

	int n = 1;
	Class<? extends Annotation> neighbourType = Mention.class;
	DIRECTION direction = DIRECTION.RIGHT;
	GroupBy toText = GroupBy.ENTITY;

	enum DIRECTION {
		LEFT, RIGHT
	};

	public AnalyzerActionPanel_Neighbour(DocumentModel documentModel, Iterable<Entity> entity) {
		super(documentModel, entity);

		init();

		refresh();
	}

	@Override
	JPanel getOptionPanel() {
		JPanel pan = super.getOptionPanel();

		// direction
		pan.add(new JLabel(Annotator.getString(Strings.ANALYZER_NEIGHBOUR_DIRECTION)));
		JComboBox<DIRECTION> directionBox = new JComboBox<DIRECTION>(DIRECTION.values());
		directionBox.setEditable(false);
		directionBox.setSelectedItem(direction);
		directionBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				direction = (DIRECTION) directionBox.getSelectedItem();
				refresh();
			}

		});
		directionBox.setRenderer(new TranslatedListCellRenderer(Strings.ANALYZER_NEIGHBOUR_DIRECTION_));
		pan.add(directionBox);

		// unit
		pan.add(new JLabel(Annotator.getString(Strings.ANALYZER_NEIGHBOUR_UNIT)));

		DefaultComboBoxModel<Class<?>> unitBoxModel = new DefaultComboBoxModel<Class<?>>();
		unitBoxModel.addElement(Mention.class);
		if (JCasUtil.exists(documentModel.getJcas(), Token.class))
			unitBoxModel.addElement(Token.class);
		if (JCasUtil.exists(documentModel.getJcas(), Sentence.class))
			unitBoxModel.addElement(Sentence.class);

		JComboBox<Class<?>> unitBox = new JComboBox<Class<?>>(unitBoxModel);
		unitBox.setSelectedItem(neighbourType);
		unitBox.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				neighbourType = (Class<? extends Annotation>) unitBox.getSelectedItem();
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
	public AnalysisAction getType() {
		return AnalysisAction.NEIGHBOUR;
	}

	@Override
	void calculateCounts() {
		ImmutableList<Mention> mentions = Lists.immutable.withAll(entities)
				.flatCollect(e -> documentModel.getCoreferenceModel().getMentions(e));
		ImmutableList<? extends Annotation> followers;
		if (direction == DIRECTION.RIGHT) {
			followers = mentions.flatCollect(m -> JCasUtil.selectFollowing(neighbourType, m, n));
		} else {
			followers = mentions.flatCollect(m -> JCasUtil.selectPreceding(neighbourType, m, n));

		}

		if (neighbourType != Mention.class)
			toText = GroupBy.COVEREDTEXT;
		switch (toText) {
		case ENTITY:
			cts = followers.selectInstancesOf(Mention.class).countBy(m -> m.getEntity().getLabel())
					.toMapOfItemToCount();
			break;
		case COVEREDTEXT:
		default:
			cts = followers.countBy(m -> m.getCoveredText()).toMapOfItemToCount();

		}

	}

}
