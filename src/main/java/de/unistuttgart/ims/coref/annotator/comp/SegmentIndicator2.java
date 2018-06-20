package de.unistuttgart.ims.coref.annotator.comp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v1.Segment;
import de.unistuttgart.ims.coref.annotator.document.SegmentModel;

public class SegmentIndicator2 extends JScrollBar implements ListDataListener {

	private static final long serialVersionUID = 1L;

	MutableList<Segment> segmentList = Lists.mutable.empty();

	int lastCharacterPosition = Integer.MAX_VALUE;

	private boolean unitIncrementSet;
	private boolean blockIncrementSet;
	JScrollPane scrollPane;

	public SegmentIndicator2(JScrollPane jsp) {
		super(JScrollBar.VERTICAL);
		this.putClientProperty("JScrollBar.fastWheelScrolling", Boolean.TRUE);
		this.scrollPane = jsp;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		Color lgray = new Color(240, 240, 240);
		Font originalFont = g2.getFont(); // Font.getFont(Font.DIALOG);

		AffineTransform affineTransform = new AffineTransform();
		affineTransform.rotate(Math.toRadians(90), 0, 0);
		Font rotatedFont = originalFont.deriveFont(affineTransform);

		g2.setColor(Color.black);
		for (Segment segment : segmentList) {
			int y = scale(segment.getBegin());
			int h = scale(segment.getEnd()) - y;
			g2.drawLine(0, y, getWidth(), y);
			g2.drawLine(0, y + h, getWidth(), y + h);
			// g2.drawRect(1, y, getWidth() - 2, h - 1);
			if (segment.getLabel() != null) {
				g2.setFont(rotatedFont);
				g2.setColor(Color.black);
				int sw = g2.getFontMetrics(originalFont).stringWidth(segment.getLabel());
				int spos = (int) (y + h * 0.5 - sw * 0.5);
				g2.drawString(segment.getLabel(), 5, spos);
				g2.setFont(originalFont);
			}

		}
	}

	private int scale(double vpos) {
		return (int) ((vpos / getLastCharacterPosition()) * getHeight());
	}

	public MutableList<Segment> getSegmentList() {
		return segmentList;
	}

	public void setSegmentList(MutableList<Segment> segmentList) {
		this.segmentList = segmentList;
	}

	public int getLastCharacterPosition() {
		return lastCharacterPosition;
	}

	public void setLastCharacterPosition(int lastCharacterPosition) {
		this.lastCharacterPosition = lastCharacterPosition;
	}

	@Override
	public void intervalAdded(ListDataEvent e) {
		if (e.getType() == ListDataEvent.INTERVAL_ADDED && e.getIndex0() != e.getIndex1())
			segmentList.addAll(e.getIndex0(),
					((SegmentModel) e.getSource()).getElementsAt(e.getIndex0(), e.getIndex1()).castToCollection());
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		for (int i = e.getIndex1() - 1; i >= e.getIndex0(); i--)
			segmentList.remove(i);
	}

	@Override
	public void contentsChanged(ListDataEvent e) {

	}

	@Override
	public void setUnitIncrement(int unitIncrement) {
		unitIncrementSet = true;
		this.putClientProperty("JScrollBar.fastWheelScrolling", null);
		super.setUnitIncrement(unitIncrement);
	}

	@Override
	public int getUnitIncrement(int direction) {
		JViewport vp = scrollPane.getViewport();
		if (!unitIncrementSet && (vp != null) && (vp.getView() instanceof Scrollable)) {
			Scrollable view = (Scrollable) (vp.getView());
			Rectangle vr = vp.getViewRect();
			return view.getScrollableUnitIncrement(vr, getOrientation(), direction);
		} else {
			return super.getUnitIncrement(direction);
		}
	}

	/**
	 * Messages super to set the value, and resets the
	 * <code>blockIncrementSet</code> instance variable to true.
	 *
	 * @param blockIncrement
	 *            the new block increment value, in pixels
	 */
	@Override
	public void setBlockIncrement(int blockIncrement) {
		blockIncrementSet = true;
		this.putClientProperty("JScrollBar.fastWheelScrolling", null);
		super.setBlockIncrement(blockIncrement);
	}

	/**
	 * Computes the block increment for scrolling if the viewport's view is a
	 * <code>Scrollable</code> object. Otherwise the <code>blockIncrement</code>
	 * equals the viewport's width or height. If there's no viewport return
	 * <code>super.getBlockIncrement</code>.
	 *
	 * @param direction
	 *            less than zero to scroll up/left, greater than zero for down/right
	 * @return an integer, in pixels, containing the block increment
	 * @see Scrollable#getScrollableBlockIncrement
	 */
	@Override
	public int getBlockIncrement(int direction) {
		JViewport vp = scrollPane.getViewport();
		if (blockIncrementSet || vp == null) {
			return super.getBlockIncrement(direction);
		} else if (vp.getView() instanceof Scrollable) {
			Scrollable view = (Scrollable) (vp.getView());
			Rectangle vr = vp.getViewRect();
			return view.getScrollableBlockIncrement(vr, getOrientation(), direction);
		} else if (getOrientation() == VERTICAL) {
			return vp.getExtentSize().height;
		} else {
			return vp.getExtentSize().width;
		}
	}

}
