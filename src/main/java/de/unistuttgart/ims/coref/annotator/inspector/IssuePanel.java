package de.unistuttgart.ims.coref.annotator.inspector;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.text.BadLocationException;

import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.weathericons.WeatherIcons;

import de.unistuttgart.ims.coref.annotator.HighlightManager;
import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.UnderlinePainter;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.inspector.Issue.InstanceIssue;

public class IssuePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	int context = 15;
	Span excerpt;
	String abbreviation = "[...]";

	JTextField textField;

	HighlightManager highlightManager;

	Issue issue;

	public IssuePanel(Issue issue) {
		this.issue = issue;
		SpringLayout layout = new SpringLayout();

		setLayout(layout);

		JLabel label = new JLabel(issue.getDescription());
		if (getIcon(issue.getIssueType()) != null)
			label.setIcon(getIcon(issue.getIssueType()));
		if (issue.getIssueType().getToolTip() != null)
			label.setToolTipText(issue.getIssueType().getToolTip());
		add(label);
		layout.putConstraint(SpringLayout.WEST, label, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, label, 10, SpringLayout.NORTH, this);

		if (issue.isSolvable()) {
			JPanel solutionsPanel = new JPanel();
			for (int i = 0; i < issue.getNumberOfSolutions(); i++) {
				final int solution = i;
				JButton solveButton = new JButton(issue.getSolutionDescription(solution));
				solveButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						issue.fireSolve(solution);
						updatePanel();
					}

				});
				issue.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (evt.getPropertyName().equals("solvable"))
							solveButton.setEnabled((boolean) evt.getNewValue());
					}

				});
				solutionsPanel.add(solveButton);
			}
			layout.putConstraint(SpringLayout.EAST, label, 10, SpringLayout.WEST, solutionsPanel);
			layout.putConstraint(SpringLayout.NORTH, solutionsPanel, 10, SpringLayout.NORTH, this);
			layout.putConstraint(SpringLayout.EAST, solutionsPanel, -10, SpringLayout.EAST, this);
		}
		if (issue instanceof InstanceIssue) {
			TOP instance = ((InstanceIssue<?>) issue).getInstance();
			if (instance instanceof Annotation) {
				Annotation annotation = (Annotation) instance;
				int length = annotation.getEnd() - annotation.getBegin();
				excerpt = new Span(Math.max(0, annotation.getBegin() - context),
						Math.min(((InstanceIssue<?>) issue).getInstance().getCAS().getDocumentText().length(),
								annotation.getEnd() + context));
				String excerptText = annotation.getCAS().getDocumentText().substring(Math.max(0, excerpt.begin),
						excerpt.end);
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
					add(textField);
					layout.putConstraint(SpringLayout.WEST, textField, 10, SpringLayout.WEST, this);
					layout.putConstraint(SpringLayout.NORTH, textField, 10, SpringLayout.SOUTH, label);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}
		}

		issue.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("solved"))
					label.setIcon(FontIcon.of(WeatherIcons.DAY_SUNNY));

			}

		});

	}

	@Override
	public Dimension getSize() {
		return new Dimension(300, 100);
	}

	public void updatePanel() {
		highlightManager.getHighlighter().removeAllHighlights();

		if (issue instanceof InstanceIssue) {
			TOP instance = ((InstanceIssue<?>) issue).getInstance();
			if (instance instanceof Annotation) {
				Annotation annotation = (Annotation) instance;
				int length = annotation.getEnd() - annotation.getBegin();

				Color color = Color.red;
				if (annotation instanceof Mention) {
					color = new Color(((Mention) annotation).getEntity().getColor());
				}
				int begin = (annotation.getBegin() - excerpt.begin) + abbreviation.length();
				int end = begin + length;
				try {
					highlightManager.getHighlighter().addHighlight(begin, end, new UnderlinePainter(color, 1));
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Icon getIcon(IssueType number) {
		switch (number) {
		case MISTAKE:
			return FontIcon.of(WeatherIcons.THUNDERSTORM);
		case QUESTIONABLE:
			return FontIcon.of(WeatherIcons.DAY_CLOUDY);
		default:
			return null;
		}
	}

}
