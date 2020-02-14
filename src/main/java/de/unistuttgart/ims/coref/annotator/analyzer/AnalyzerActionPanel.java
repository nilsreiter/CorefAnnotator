package de.unistuttgart.ims.coref.annotator.analyzer;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public abstract class AnalyzerActionPanel extends JPanel {

	static enum ACTION {
		MENTION, NEIGHBOUR_RIGHT, TEXTLOCATION, DUMMY;

		AnalyzerActionPanel getObject(DocumentModel documentModel, Iterable<Entity> entity) {
			switch (this) {
			case TEXTLOCATION:
				return new AnalyzerActionPanel_TextLocation(documentModel, entity);
			case NEIGHBOUR_RIGHT:
				return new AnalyzerActionPanel_NeighbourRight(documentModel, entity);
			case MENTION:
				return new AnalyzerActionPanel_Mention(documentModel, entity);
			default:
				return new AnalyzerActionPanel_Dummy(documentModel, entity);
			}
		}
	};

	private static final long serialVersionUID = 1L;

	DocumentModel documentModel;
	protected int gap = 5;
	protected SpringLayout layout;

	public AnalyzerActionPanel(DocumentModel documentModel, Iterable<Entity> entity) {
		this.documentModel = documentModel;

		setPreferredSize(new Dimension(400, 800));

		layout = new SpringLayout();
		setLayout(layout);
	}

	void chartConstraints(Component c) {
		layout.putConstraint(SpringLayout.WEST, c, gap, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, c, gap, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.EAST, this, gap, SpringLayout.EAST, c);
	}

	public abstract ACTION getType();

	public abstract void setEntities(Iterable<Entity> entities);

}
