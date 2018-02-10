package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

class Caret extends DefaultCaret implements FocusListener {
	private static final Highlighter.HighlightPainter focusedPainter = new DefaultHighlighter.DefaultHighlightPainter(
			Color.YELLOW);

	private static final long serialVersionUID = 1L;

	@Override
	protected Highlighter.HighlightPainter getSelectionPainter() {
		setBlinkRate(500);
		return focusedPainter;
	}

	@Override
	public void setSelectionVisible(boolean visible) {
		super.setSelectionVisible(true);
	}

	@Override
	public void focusGained(FocusEvent e) {
		super.setVisible(true);
		super.setSelectionVisible(true);
	};

	@Override
	public void focusLost(FocusEvent e) {
		super.setVisible(true);
		super.setSelectionVisible(true);
	};

}