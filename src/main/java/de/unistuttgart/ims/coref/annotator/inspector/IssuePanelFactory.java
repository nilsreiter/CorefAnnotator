package de.unistuttgart.ims.coref.annotator.inspector;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import de.unistuttgart.ims.coref.annotator.comp.PanelFactory;

public class IssuePanelFactory implements PanelFactory<Issue, JPanel>, ListCellRenderer<Issue> {

	@Override
	public JPanel getPanel(Issue object) {

		return new IssuePanel(object);

	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Issue> list, Issue value, int index,
			boolean isSelected, boolean cellHasFocus) {
		return new IssuePanel(value);
	}

}
