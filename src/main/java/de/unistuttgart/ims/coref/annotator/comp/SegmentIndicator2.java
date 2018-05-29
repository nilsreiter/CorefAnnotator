package de.unistuttgart.ims.coref.annotator.comp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v1.Segment;
import de.unistuttgart.ims.coref.annotator.document.SegmentModel;

public class SegmentIndicator2 extends JPanel implements ListDataListener {

	private static final long serialVersionUID = 1L;

	MutableList<Segment> segmentList = Lists.mutable.empty();

	int lastCharacterPosition = Integer.MAX_VALUE;

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		Color lgray = new Color(240, 240, 240);

		g2.setFont(Font.getFont(Font.SERIF));

		for (Segment segment : segmentList) {
			g.setColor(Color.red);
			int y = scale(segment.getBegin());
			int h = scale(segment.getEnd()) - y;
			g.fillRect(0, y, 50, h);

		}
	}

	@Override
	public int getWidth() {
		return 50;
	}

	private int scale(double vpos) {
		return (int) (vpos / getHeight());
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

}
