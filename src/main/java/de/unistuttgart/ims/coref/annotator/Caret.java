package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;

import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

class Caret extends DefaultCaret {
	private static final Highlighter.HighlightPainter focusedPainter = new DefaultHighlighter.DefaultHighlightPainter(
			Color.YELLOW);

	private static final long serialVersionUID = 1L;

	@Override
	protected Highlighter.HighlightPainter getSelectionPainter() {
		setBlinkRate(500);
		return focusedPainter;
	}

}