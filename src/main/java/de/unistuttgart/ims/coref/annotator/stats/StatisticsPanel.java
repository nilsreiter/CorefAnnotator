package de.unistuttgart.ims.coref.annotator.stats;

import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Util;

public class StatisticsPanel extends JPanel implements DocumentStatisticsListener {

	private static final long serialVersionUID = 1L;

	DocumentStatistics documentStatistics;

	ArrayList<JLabel> values;

	Font valueFont;

	int valueWidth = 10;

	public StatisticsPanel() {
		super();
		valueFont = new Font(Font.MONOSPACED, Font.PLAIN, this.getFont().getSize());

		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		setLayout(layout);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		ParallelGroup labelLabels = layout.createParallelGroup();
		ParallelGroup valueLabels = layout.createParallelGroup();
		values = new ArrayList<JLabel>();
		for (DocumentStatistics.Property property : DocumentStatistics.Property.values()) {

			// add label
			JLabel propertyLabel = new JLabel(Annotator.getString(DocumentStatistics.PREFIX + "." + property.name()));
			labelLabels.addComponent(propertyLabel);

			// add value
			JLabel label = new JLabel("");
			label.setFont(valueFont);
			values.add(label);
			valueLabels.addComponent(label);
			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE).addComponent(propertyLabel).addComponent(label));

		}
		hGroup.addGroup(labelLabels);
		hGroup.addGroup(valueLabels);
		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);
	}

	@Override
	public void refreshStatistics() {
		for (int i = 0; i < DocumentStatistics.Property.values().length; i++) {
			String fmtString = documentStatistics.getFormatString(DocumentStatistics.Property.values()[i]);
			Object val = documentStatistics.getValue(DocumentStatistics.Property.values()[i]);

			values.get(i).setText(StringUtils.leftPad(Util.format("%1" + fmtString, val), valueWidth));
		}

		repaint();
	}

	public DocumentStatistics getDocumentStatistics() {
		return documentStatistics;
	}

	public void setDocumentStatistics(DocumentStatistics documentStatistics) {
		this.documentStatistics = documentStatistics;
		this.documentStatistics.addDocumentStatisticsListener(this);
		refreshStatistics();
	}

}
