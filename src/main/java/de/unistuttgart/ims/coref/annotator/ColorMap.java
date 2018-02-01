package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;
import java.util.HashMap;

import de.unistuttgart.ims.coref.annotator.api.Entity;

public class ColorMap extends HashMap<Entity, Color> {

	Color[] colors = new Color[] { Color.BLUE, Color.RED, Color.GREEN, Color.CYAN, Color.MAGENTA, Color.YELLOW };

	private static final long serialVersionUID = 1L;

	int nextColor = 0;

	@Override
	public Color get(Object e) {
		if (!(e instanceof Entity))
			return null;
		if (this.containsKey(e))
			return super.get(e);
		Color c = colors[nextColor++ % colors.length];
		super.put((Entity) e, c);
		return c;
	}

}
