package de.unistuttgart.ims.coref.annotator;

import java.awt.Dimension;

import javax.swing.JPanel;

public class SegmentPanel extends JPanel {

	int height = 0;

	private static final long serialVersionUID = 1L;

	@Override
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(getWidth(), getHeight());
	}

}
