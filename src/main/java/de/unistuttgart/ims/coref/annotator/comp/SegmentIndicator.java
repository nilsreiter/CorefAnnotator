package de.unistuttgart.ims.coref.annotator.comp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import de.unistuttgart.ims.coref.annotator.SegmentPanel;
import de.unistuttgart.ims.coref.annotator.api.v1.Segment;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public class SegmentIndicator extends PanelList<Segment, SegmentPanel> implements AdjustmentListener {

	int documentLength = 100;

	public SegmentIndicator() {
		super();
		setFactory(new SegmentPanelFactory());
	}

	private static final long serialVersionUID = 1L;

	DocumentModel documentModel;

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {

	}

	public DocumentModel getDocumentModel() {
		return documentModel;
	}

	public void setDocumentModel(DocumentModel documentModel) {
		this.documentModel = documentModel;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(getWidth(), getHeight());
	}

	@Override
	public int getWidth() {
		return 30;
	}

	public class SegmentPanelFactory implements PanelFactory<Segment, SegmentPanel> {

		@Override
		public SegmentPanel getPanel(Segment object) {
			double length = object.getEnd() - object.getBegin();

			SegmentPanel panel = new SegmentPanel();
			if (object.getLabel() != null)
				panel.add(new JLabel("Segment: " + object.getLabel()));
			else
				panel.add(new JLabel("Segment"));
			panel.setLength(length / getDocumentLength());
			panel.setBorder(BorderFactory.createLineBorder(Color.blue));
			panel.setEnabled(true);
			panel.setVisible(true);
			return panel;
		}

	}

	public int getDocumentLength() {
		return documentLength;
	}

	public void setDocumentLength(int documentLength) {
		this.documentLength = documentLength;
	}

}
