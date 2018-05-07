package de.unistuttgart.ims.coref.annotator.inspector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.unistuttgart.ims.coref.annotator.comp.PanelFactory;

public class IssuePanelFactory implements PanelFactory<Issue, JPanel> {

	@Override
	public JPanel getPanel(Issue object) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(new JLabel(object.getDescription()));

		JButton solveButton = new JButton("solve");
		solveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				object.solve();
			}

		});

		panel.add(solveButton);
		panel.setSize(300, 100);
		return panel;
	}

}
