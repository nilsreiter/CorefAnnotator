package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;

public class ColorProvider {

	float x = 0;
	float increment = 183f;
	float numberOfColors = 360f;

	public Color getNextColor() {
		x = (x + increment) % numberOfColors;
		return Color.getHSBColor(x / numberOfColors, 1, 1);
	}

}
