package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.Mention;

class HighlightManager {
	JTextComponent textComponent;
	Highlighter hilit;
	Highlighter.HighlightPainter painter;

	Map<Annotation, Object> highlightMap = new HashMap<Annotation, Object>();
	RangedCounter spanCounter = new RangedCounter();

	public HighlightManager(JTextComponent component) {
		hilit = new DefaultHighlighter();
		textComponent = component;
		textComponent.setHighlighter(hilit);
	}

	public void draw(Annotation a) {
		if (a instanceof Mention)
			draw((Mention) a);
	}

	public void draw(Mention m) {
		draw(m, new Color(m.getEntity().getColor()), false, true);
		if (m.getDiscontinuous() != null)
			draw(m.getDiscontinuous(), new Color(m.getEntity().getColor()), true, true);

	}

	public void draw(Mention m, boolean repaint) {
		draw(m, new Color(m.getEntity().getColor()), false, false);
		if (m.getDiscontinuous() != null)
			draw(m.getDiscontinuous(), new Color(m.getEntity().getColor()), true, false);
	}

	public void clearAndDrawAllAnnotations(JCas jcas) {
		hilit.removeAllHighlights();
		highlightMap.clear();
		spanCounter.clear();
		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			draw(m, new Color(m.getEntity().getColor()), false, false);
			if (m.getDiscontinuous() != null)
				draw(m.getDiscontinuous(), new Color(m.getEntity().getColor()), true, false);

		}
		textComponent.repaint();
	}

	public void undraw(Annotation a) {
		Object hi = highlightMap.get(a);
		Span span = new Span(a);
		if (span != null)
			spanCounter.subtract(span, hi);
		if (hi != null)
			hilit.removeHighlight(hi);
	}

	protected void draw(Annotation a, Color c, boolean dotted, boolean repaint) {
		Object hi = highlightMap.get(a);
		Span span = new Span(a);
		if (hi != null) {
			spanCounter.subtract(span, hi);
			hilit.removeHighlight(hi);
		}
		try {
			int n = spanCounter.add(span, hi);
			hi = hilit.addHighlight(a.getBegin(), a.getEnd(), new UnderlinePainter(c, n * 3, dotted));

			highlightMap.put(a, hi);
			// TODO: this is overkill, but didn't work otherwise
			if (repaint)
				textComponent.repaint();

		} catch (BadLocationException e) {
			Annotator.logger.catching(e);
		}
	}

	public Highlighter getHighlighter() {
		return hilit;
	}

}