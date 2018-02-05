package de.unistuttgart.ims.coref.annotator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;

public class UnderlinePainter extends DefaultHighlighter.DefaultHighlightPainter {
	int lineWidth = 3;
	int downDistance = 0;

	public UnderlinePainter(Color color, int distance) {
		super(color);
		this.downDistance = distance;
	}

	/**
	 * Paints a portion of a highlight.
	 *
	 * @param g
	 *            the graphics context
	 * @param offs0
	 *            the starting model offset >= 0
	 * @param offs1
	 *            the ending model offset >= offs1
	 * @param bounds
	 *            the bounding box of the view, which is not necessarily the
	 *            region to paint.
	 * @param c
	 *            the editor
	 * @param view
	 *            View painting for
	 * @return region drawing occurred in
	 */
	@Override
	public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
		Rectangle r = getDrawingArea(offs0, offs1, bounds, view);

		Graphics2D g2 = (Graphics2D) g;

		if (r == null)
			return null;

		// Do your custom painting
		Color color = getColor();
		g.setColor(color == null ? c.getSelectionColor() : color);

		g2.setStroke(new BasicStroke(lineWidth));
		g2.drawLine(r.x, r.y + r.height + downDistance, r.x + r.width, r.y + r.height + downDistance);

		r.setBounds(r.x - lineWidth, r.y, r.width + 2 * lineWidth, r.height + lineWidth + downDistance);
		return r;
	}

	private Rectangle getDrawingArea(int offs0, int offs1, Shape bounds, View view) {
		// Contained in view, can just use bounds.

		if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
			Rectangle alloc;

			if (bounds instanceof Rectangle) {
				alloc = (Rectangle) bounds;
			} else {
				alloc = bounds.getBounds();
			}

			return alloc;
		} else {
			// Should only render part of View.
			try {
				// --- determine locations ---
				Shape shape = view.modelToView(offs0, Position.Bias.Forward, offs1, Position.Bias.Backward, bounds);
				Rectangle r = (shape instanceof Rectangle) ? (Rectangle) shape : shape.getBounds();

				return r;
			} catch (BadLocationException e) {
				// can't render
			}
		}

		// Can't render

		return null;
	}

}
