package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;

public class ColorProvider {

	float x = 0;
	float increment = 0.618033988749895f;
	float numberOfColors = 1f;

	public Color getNextColor() {
		x = (x + increment) % numberOfColors;
		return Color.getHSBColor(x / numberOfColors, 1, 1);
	}

}
