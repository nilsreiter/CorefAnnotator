package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;

public class ColorMap {

	Color[] colors = new Color[] { Color.BLUE, Color.RED, Color.GREEN, Color.CYAN, Color.MAGENTA, Color.YELLOW };

	int nextColor = 0;

	float lowest = 0.2f;
	float highest = 0.8f;

	float r = lowest;
	float g = 0.5f;
	float b = highest;
	float delta = 0.1f;

	public Color getNextColor() {
		Color c = new Color(r, g, b);

		r += delta;
		if (r > highest)
			r = lowest;
		g += delta;
		if (g > highest)
			g = lowest;
		b -= delta;
		if (b < lowest)
			b = highest;

		return c;
		// return colors[nextColor++ % colors.length];
	}

}
