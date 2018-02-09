package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;

public class ColorProvider {

	float x = 0;
	float increment = 3f;

	public Color getNextColor() {
		x += increment;
		return Color.getHSBColor(x / 360, 1, 1);
	}

}
