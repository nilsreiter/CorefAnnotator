package de.unistuttgart.ims.coref.annotator.analyzer;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public abstract class AnalyzerActionPanel extends JPanel {

	static enum ACTION {
		MENTION, NEIGHBOUR, TEXTLOCATION, DUMMY;

		AnalyzerActionPanel getObject(DocumentModel documentModel, Iterable<Entity> entity) {
			switch (this) {
			case TEXTLOCATION:
				return new AnalyzerActionPanel_TextLocation(documentModel, entity);
			case NEIGHBOUR:
				return new AnalyzerActionPanel_Neighbour(documentModel, entity);
			case MENTION:
				return new AnalyzerActionPanel_Mention(documentModel, entity);
			default:
				return new AnalyzerActionPanel_Dummy(documentModel, entity);
			}
		}
	};

	private static final long serialVersionUID = 1L;

	DocumentModel documentModel;
	int gap = 5;
	SpringLayout layout;
	JPanel optionPanel;
	int chartWidth = 700;
	int chartHeight = 500;

	public AnalyzerActionPanel(DocumentModel documentModel, Iterable<Entity> entity) {
		this.documentModel = documentModel;
	}

	void chartConstraints(Component c) {
		layout.putConstraint(SpringLayout.WEST, c, gap, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, c, gap, SpringLayout.SOUTH, optionPanel);
		layout.putConstraint(SpringLayout.EAST, this, gap, SpringLayout.EAST, c);
	}

	void init() {
		setPreferredSize(new Dimension(700, 800));

		layout = new SpringLayout();
		setLayout(layout);
		JLabel headerLabel = new JLabel(Annotator.getString(Strings.ANALZYER_ACTIONS_ + getType().toString()));
		add(headerLabel);
		layout.putConstraint(SpringLayout.NORTH, headerLabel, gap, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, headerLabel, gap, SpringLayout.WEST, this);

		optionPanel = getOptionPanel();
		add(optionPanel);

		layout.putConstraint(SpringLayout.NORTH, optionPanel, gap, SpringLayout.SOUTH, headerLabel);
		layout.putConstraint(SpringLayout.EAST, this, gap, SpringLayout.EAST, optionPanel);
		layout.putConstraint(SpringLayout.WEST, optionPanel, gap, SpringLayout.WEST, this);
	}

	public abstract ACTION getType();

	public abstract void setEntities(Iterable<Entity> entities);

	JPanel getOptionPanel() {
		return new JPanel();
	};

}
