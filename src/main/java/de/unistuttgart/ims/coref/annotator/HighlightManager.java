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

import de.unistuttgart.ims.coref.annotator.api.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.Mention;

class HighlightManager {
	Map<Annotation, Object> highlightMap = new HashMap<Annotation, Object>();
	DefaultHighlighter hilit;

	RangedCounter spanCounter = new RangedCounter();
	JTextComponent textComponent;

	public HighlightManager(JTextComponent component) {
		hilit = new DefaultHighlighter();
		hilit.setDrawsLayeredHighlights(false);
		textComponent = component;
		textComponent.setHighlighter(hilit);
	}

	@Deprecated
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

	public void highlight(Annotation a) {
		draw(a, new Color(255, 255, 150), false, false,
				new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 255, 200)));
	}

	public void underline(Annotation a) {
		if (a instanceof Mention)
			underline((Mention) a);
		else if (a instanceof DetachedMentionPart)
			underline((DetachedMentionPart) a);
	}

	public void underline(DetachedMentionPart dmp) {
		hilit.setDrawsLayeredHighlights(true);
		draw(dmp, new Color(dmp.getMention().getEntity().getColor()), true, true, null);
		hilit.setDrawsLayeredHighlights(false);
	}

	public void underline(Mention m) {
		hilit.setDrawsLayeredHighlights(true);
		draw(m, new Color(m.getEntity().getColor()), false, true, null);
		if (m.getDiscontinuous() != null)
			draw(m.getDiscontinuous(), new Color(m.getEntity().getColor()), true, true, null);
		hilit.setDrawsLayeredHighlights(false);
	}

	public void underline(Annotation m, Color color) {
		if (m instanceof Mention)
			underline((Mention) m, color);
		else {
			hilit.setDrawsLayeredHighlights(true);
			draw(m, color, false, true, null);
			hilit.setDrawsLayeredHighlights(false);
		}
	}

	public void underline(Mention m, Color color) {
		hilit.setDrawsLayeredHighlights(true);
		draw(m, color, false, true, null);
		if (m.getDiscontinuous() != null)
			draw(m.getDiscontinuous(), color, true, true, null);
		hilit.setDrawsLayeredHighlights(false);
	}

	public void underline(Mention m, boolean repaint) {
		hilit.setDrawsLayeredHighlights(true);
		draw(m, new Color(m.getEntity().getColor()), false, false, null);
		if (m.getDiscontinuous() != null)
			draw(m.getDiscontinuous(), new Color(m.getEntity().getColor()), true, false, null);
		hilit.setDrawsLayeredHighlights(false);
	}

	public void undraw(Annotation a) {
		Object hi = highlightMap.get(a);
		Span span = new Span(a);
		if (span != null)
			spanCounter.subtract(span, hi);
		if (hi != null)
			hilit.removeHighlight(hi);
	}

}