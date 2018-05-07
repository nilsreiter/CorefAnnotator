package de.unistuttgart.ims.coref.annotator.inspector;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.text.BadLocationException;

import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.HighlightManager;
import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.UnderlinePainter;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.comp.PanelFactory;
import de.unistuttgart.ims.coref.annotator.inspector.Issue.InstanceIssue;

public class IssuePanelFactory implements PanelFactory<Issue, JPanel> {

	int context = 15;

	String abbreviation = "[...]";

	JTextField textField;

	Span excerpt;

	HighlightManager highlightManager;

	@Override
	public JPanel getPanel(Issue object) {
		JPanel panel = new JPanel();
		SpringLayout layout = new SpringLayout();

		panel.setLayout(layout);

		JLabel label = new JLabel(object.getDescription());
		panel.add(label);
		layout.putConstraint(SpringLayout.WEST, label, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, label, 10, SpringLayout.NORTH, panel);

		if (object.isSolvable()) {
			JButton solveButton = new JButton("solve");
			solveButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					object.solve();
					updatePanel(object);
				}

			});
			object.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals("solvable"))
						solveButton.setEnabled((boolean) evt.getNewValue());
				}

			});
			panel.add(solveButton);
			layout.putConstraint(SpringLayout.EAST, label, 10, SpringLayout.WEST, solveButton);
			layout.putConstraint(SpringLayout.NORTH, solveButton, 10, SpringLayout.NORTH, panel);
			layout.putConstraint(SpringLayout.EAST, solveButton, -10, SpringLayout.EAST, panel);

		}
		if (object instanceof InstanceIssue) {
			TOP instance = ((InstanceIssue<?>) object).getInstance();
			if (instance instanceof Annotation) {
				Annotation annotation = (Annotation) instance;
				int length = annotation.getEnd() - annotation.getBegin();
				excerpt = new Span(annotation.getBegin() - context, annotation.getEnd() + context);
				String excerptText = annotation.getCAS().getDocumentText().substring(excerpt.begin, excerpt.end);
				textField = new JTextField(abbreviation + excerptText + abbreviation);
				highlightManager = new HighlightManager(textField);
				highlightManager.getHighlighter().setDrawsLayeredHighlights(true);

				try {
					Color color = Color.red;
					if (annotation instanceof Mention) {
						color = new Color(((Mention) annotation).getEntity().getColor());
					}

					highlightManager.getHighlighter().addHighlight(context + abbreviation.length(),
							context + abbreviation.length() + length, new UnderlinePainter(color, 1));

					textField.setEditable(false);
					panel.add(textField);
					layout.putConstraint(SpringLayout.WEST, textField, 10, SpringLayout.WEST, panel);
					layout.putConstraint(SpringLayout.NORTH, textField, 10, SpringLayout.SOUTH, label);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}
		}

		panel.setSize(300, 100);
		return panel;
	}

	public void updatePanel(Issue object) {
		highlightManager.getHighlighter().removeAllHighlights();

		if (object instanceof InstanceIssue) {
			TOP instance = ((InstanceIssue<?>) object).getInstance();
			if (instance instanceof Annotation) {
				Annotation annotation = (Annotation) instance;
				int length = annotation.getEnd() - annotation.getBegin();

				Color color = Color.red;
				if (annotation instanceof Mention) {
					color = new Color(((Mention) annotation).getEntity().getColor());
				}
				int begin = annotation.getBegin() - excerpt.begin + abbreviation.length();
				int end = begin + length;
				try {
					highlightManager.getHighlighter().addHighlight(begin, end, new UnderlinePainter(color, 1));
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
