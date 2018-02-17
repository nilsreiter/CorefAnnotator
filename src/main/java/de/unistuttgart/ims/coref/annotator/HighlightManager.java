package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.Mention;

class HighlightManager {
	JTextComponent textComponent;
	Highlighter hilit;

	Map<Annotation, Object> highlightMap = new HashMap<Annotation, Object>();
	RangedCounter spanCounter = new RangedCounter();

	public HighlightManager(JTextComponent component) {
		hilit = new DefaultHighlighter();
		textComponent = component;
		textComponent.setHighlighter(hilit);
	}

	/**
	 * @deprecated Use {@link #underline(Annotation)} instead
	 */
	@Deprecated
	public void draw(Annotation a) {
		underline(a);
	}

	public void underline(Annotation a) {
		if (a instanceof Mention)
			underline((Mention) a);
	}

	/**
	 * @deprecated Use {@link #underline(Mention)} instead
	 */
	@Deprecated
	public void draw(Mention m) {
		underline(m);
	}

	public void underline(Mention m) {
		draw(m, new Color(m.getEntity().getColor()), false, true, null);
		if (m.getDiscontinuous() != null)
			draw(m.getDiscontinuous(), new Color(m.getEntity().getColor()), true, true, null);

	}

	/**
	 * @deprecated Use {@link #underline(Mention,boolean)} instead
	 */
	@Deprecated
	public void draw(Mention m, boolean repaint) {
		underline(m, repaint);
	}

	public void underline(Mention m, boolean repaint) {
		draw(m, new Color(m.getEntity().getColor()), false, false, null);
		if (m.getDiscontinuous() != null)
			draw(m.getDiscontinuous(), new Color(m.getEntity().getColor()), true, false, null);
	}

	public void clearAndDrawAllAnnotations(JCas jcas) {
		hilit.removeAllHighlights();
		highlightMap.clear();
		spanCounter.clear();
		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			draw(m, new Color(m.getEntity().getColor()), false, false, null);
			if (m.getDiscontinuous() != null)
				draw(m.getDiscontinuous(), new Color(m.getEntity().getColor()), true, false, null);

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

	protected void draw(Annotation a, Color c, boolean dotted, boolean repaint,
			LayeredHighlighter.LayerPainter painter) {
		Object hi = highlightMap.get(a);
		Span span = new Span(a);
		if (hi != null) {
			spanCounter.subtract(span, hi);
			hilit.removeHighlight(hi);
		}
		try {
			int n = spanCounter.getNextLevel(span);
			if (painter == null)
				hi = hilit.addHighlight(a.getBegin(), a.getEnd(), new UnderlinePainter(c, n * 3, dotted));
			else
				hi = hilit.addHighlight(a.getBegin(), a.getEnd(), painter);
			spanCounter.add(span, hi, n);
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