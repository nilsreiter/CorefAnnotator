package de.unistuttgart.ims.coref.annotator.analyzer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.comp.SpringUtilities;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public abstract class AnalyzerActionPanel_Neighbour extends AnalyzerActionPanel_ChartTable {

	private static final long serialVersionUID = 1L;

	int n = 1;
	Class<? extends Annotation> neighbourType = Mention.class;
	DIRECTION direction = DIRECTION.RIGHT;
	TOTEXT toText = TOTEXT.ENTITY;

	enum DIRECTION {
		LEFT, RIGHT
	};

	enum TOTEXT {
		COVEREDTEXT, ENTITY
	}

	public AnalyzerActionPanel_Neighbour(DocumentModel documentModel, Iterable<Entity> entity) {
		super(documentModel, entity);
	}

	@Override
	public void setEntities(Iterable<Entity> entities) {
		ImmutableList<Mention> mentions = Lists.immutable.withAll(entities)
				.flatCollect(e -> documentModel.getCoreferenceModel().getMentions(e));
		ImmutableList<? extends Annotation> followers;
		if (direction == DIRECTION.RIGHT) {
			followers = mentions.flatCollect(m -> JCasUtil.selectFollowing(neighbourType, m, n));
		} else {
			followers = mentions.flatCollect(m -> JCasUtil.selectPreceding(neighbourType, m, n));

		}
		MutableMapIterable<String, Integer> cts;

		switch (toText) {
		case ENTITY:
			cts = followers.selectInstancesOf(Mention.class).countBy(m -> m.getEntity().getLabel())
					.toMapOfItemToCount();
			break;
		case COVEREDTEXT:
		default:
			cts = followers.countBy(m -> m.getCoveredText()).toMapOfItemToCount();

		}

		setFullData(cts);

	}

	@Override
	JPanel getOptionPanel() {
		JPanel pan = new JPanel();
		pan.setLayout(new SpringLayout());

		JLabel lab = new JLabel("Group below");
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

		pan.add(new JLabel("Direction"));
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

		pan.add(directionBox);

		SpringUtilities.makeGrid(pan, 2, 2, // rows, cols
				0, 0, // initialX, initialY
				5, 5);// xPad, yPad
		return pan;
	}

}
