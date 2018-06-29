package de.unistuttgart.ims.coref.annotator.comp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

public class CircleColorIcon extends ColorIcon {

	public CircleColorIcon() {
		this(16);
	}

	public CircleColorIcon(Color color) {
		this(16, color);
	}

	// ---------------------------------------------------------------------------

	public CircleColorIcon(int w) {
		this(w, Color.black);
	}

	// ---------------------------------------------------------------------------

	public CircleColorIcon(int width, Color c) {
		iWidth = width;

		color = c;
		border = c;
		insets = new Insets(1, 1, 1, 1);
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {

		x += insets.left;
		y += insets.top;

		int w = iWidth - insets.left - insets.right;

		g.setColor(color);
		g.fillOval(x, y, w, w);
	}
}
