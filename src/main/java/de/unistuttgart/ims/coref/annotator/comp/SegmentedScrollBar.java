package de.unistuttgart.ims.coref.annotator.comp;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v1.Segment;
import de.unistuttgart.ims.coref.annotator.document.SegmentModel;

public class SegmentedScrollBar<T extends Annotation> extends JScrollBar implements ListDataListener {

	private static final long serialVersionUID = 1L;

	MutableList<T> segmentList = Lists.mutable.empty();

	int lastCharacterPosition = Integer.MAX_VALUE;

	private boolean unitIncrementSet;
	private boolean blockIncrementSet;
	JScrollPane scrollPane;

	public SegmentedScrollBar(JScrollPane jsp) {
		super(JScrollBar.VERTICAL);
		this.putClientProperty("JScrollBar.fastWheelScrolling", Boolean.TRUE);
		this.scrollPane = jsp;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		Font originalFont = new Font(Font.DIALOG, Font.PLAIN, 12); // g2.getFont().der.deriveFont(Font.SANS_SERIF);
		FontMetrics fm = g2.getFontMetrics(originalFont);

		AffineTransform affineTransform = new AffineTransform();
		affineTransform.rotate(Math.toRadians(90), 0, 0);
		Font rotatedFont = originalFont.deriveFont(affineTransform);

		g2.setColor(Color.black);
		g2.setFont(rotatedFont);
		for (T segment : segmentList) {
			int y = scale(segment.getBegin());
			int h = scale(segment.getEnd()) - y;
			g2.drawLine(0, y, getWidth(), y);
			g2.drawLine(0, y + h, getWidth(), y + h);
			if (segment instanceof Segment) {
				Segment seg = (Segment) segment;
				if (seg.getLabel() != null) {
					int sw = fm.stringWidth(seg.getLabel());
					int spos = (int) (y + h * 0.5 - sw * 0.5);
					g2.drawString(seg.getLabel(), 3, spos);
				}
			}

		}
	}

	private int scale(double vpos) {
		int maxDoc = getLastCharacterPosition();
		int maxView = getHeight();
		try {
			Rectangle2D r = ((JTextComponent) scrollPane.getViewport().getView()).modelToView((int) vpos);
			vpos = r.getY();
			maxDoc = getMaximum();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		return (int) ((vpos / maxDoc) * maxView);
	}

	public MutableList<T> getSegmentList() {
		return segmentList;
	}

	public void setSegmentList(MutableList<T> segmentList) {
		this.segmentList = segmentList;
	}

	public int getLastCharacterPosition() {
		return lastCharacterPosition;
	}

	public void setLastCharacterPosition(int lastCharacterPosition) {
		this.lastCharacterPosition = lastCharacterPosition;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void intervalAdded(ListDataEvent e) {
		if (e.getType() == ListDataEvent.INTERVAL_ADDED && e.getIndex0() != e.getIndex1())
			segmentList.addAll(e.getIndex0(), (Collection<? extends T>) ((SegmentModel) e.getSource())
					.getElementsAt(e.getIndex0(), e.getIndex1()).castToCollection());
		repaint();
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		for (int i = e.getIndex1() - 1; i >= e.getIndex0(); i--)
			segmentList.remove(i);
		repaint();

	}

	@SuppressWarnings("unchecked")
	@Override
	public void contentsChanged(ListDataEvent e) {
		segmentList.clear();
		segmentList.addAll(
				(Collection<? extends T>) ((SegmentModel) e.getSource()).getTopLevelSegments().castToCollection());
		repaint();
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
