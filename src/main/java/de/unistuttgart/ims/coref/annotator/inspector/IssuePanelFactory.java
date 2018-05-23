package de.unistuttgart.ims.coref.annotator.inspector;

import javax.swing.JPanel;

import de.unistuttgart.ims.coref.annotator.comp.PanelFactory;

public class IssuePanelFactory implements PanelFactory<Issue, JPanel> {

	@Override
	public JPanel getPanel(Issue object) {

		return new IssuePanel(object);

	}

}
