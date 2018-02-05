package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class ColorIcon implements Icon {
	private static final int size = 20;

	private Color color = Color.black;

	public ColorIcon() {
		super();
	}

	public ColorIcon(Color color) {
		this();
		this.color = color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics,
	 * int, int)
	 */
	@Override
	public void paintIcon(Component arg0, Graphics graphics, int x, int y) {
		graphics.setColor(this.color);
		graphics.fill3DRect(x, y, size, size, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#getIconWidth()
	 */
	@Override
	public int getIconWidth() {
		return size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#getIconHeight()
	 */
	@Override
	public int getIconHeight() {
		return size;
	}
}
