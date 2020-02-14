package de.unistuttgart.ims.coref.annotator.analyzer;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public abstract class AnalyzerActionPanel extends JPanel {

	static enum ACTION {
		MENTION;

		AnalyzerActionPanel getObject(DocumentModel documentModel, Iterable<Entity> entity) {
			switch (this) {
			case MENTION:
				return new AnalyzerActionPanel_Mention(documentModel, entity);
			default:
				return null;
			}
		}
	};

	private static final long serialVersionUID = 1L;

	DocumentModel documentModel;
	int gap = 5;
	SpringLayout layout;

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
