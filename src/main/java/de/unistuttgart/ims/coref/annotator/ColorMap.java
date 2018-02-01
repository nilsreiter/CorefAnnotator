package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;

public class ColorMap {

	Color[] colors = new Color[] { Color.BLUE, Color.RED, Color.GREEN, Color.CYAN, Color.MAGENTA, Color.YELLOW };

	int nextColor = 0;

	public Color getNextColor() {
		return colors[nextColor++ % colors.length];
	}

}
