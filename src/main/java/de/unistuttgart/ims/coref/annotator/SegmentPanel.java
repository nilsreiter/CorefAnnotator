package de.unistuttgart.ims.coref.annotator;

import java.awt.Dimension;

import javax.swing.JPanel;

public class SegmentPanel extends JPanel {

	double relativeLength = 0;

	private static final long serialVersionUID = 1L;

	@Override
	public int getHeight() {
		return (int) (relativeLength * getParent().getHeight());
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(getWidth(), getHeight());
	}

	public double getLength() {
		return relativeLength;
	}

	public void setLength(double length) {
		this.relativeLength = length;
	}

}
