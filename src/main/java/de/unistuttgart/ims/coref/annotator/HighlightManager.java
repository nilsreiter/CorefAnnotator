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

import de.unistuttgart.ims.coref.annotator.api.v2.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.api.v2.MentionSurface;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

class HighlightManager {
	Map<Annotation, Object> underlineMap = new HashMap<Annotation, Object>();
	Map<Annotation, Object> highlightMap = new HashMap<Annotation, Object>();
	DefaultHighlighter hilit;

	RangedCounter spanCounter = new RangedCounter();
	JTextComponent textComponent;
	DocumentModel documentModel;

	public HighlightManager(JTextComponent component) {
		hilit = new DefaultHighlighter();
		hilit.setDrawsLayeredHighlights(false);
		textComponent = component;
		textComponent.setHighlighter(hilit);
	}

	@Deprecated
	public void clearAndDrawAllAnnotations(JCas jcas) {
		hilit.removeAllHighlights();
		underlineMap.clear();
		spanCounter.clear();
		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			for (MentionSurface ms : m.getSurface())
				highlight(ms, new Color(m.getEntity().getColor()), false, false, null);
			if (m.getDiscontinuous() != null)
				highlight(m.getDiscontinuous(), new Color(m.getEntity().getColor()), true, false, null);

		}
		textComponent.repaint();
	}

	protected void underline(Annotation a, Color c, boolean dotted, boolean repaint) {
		Span span = new Span(a);

		try {
			if (underlineMap.containsKey(a)) {
				Object hi = underlineMap.get(a);
				spanCounter.subtract(span, hi);
				hilit.removeHighlight(hi);
			}
			int n = spanCounter.getNextLevel(span);
			Object hi = hilit.addHighlight(a.getBegin(), a.getEnd(), new UnderlinePainter(c, n * 3, dotted));
			spanCounter.add(span, hi, n);
			underlineMap.put(a, hi);
			// TODO: this is overkill, but didn't work otherwise
			if (repaint)
				textComponent.repaint();
		} catch (BadLocationException e) {
			Annotator.logger.catching(e);
		}
	}

	protected void highlight(Annotation a, Color c, boolean dotted, boolean repaint,
			LayeredHighlighter.LayerPainter painter) {
		if (painter == null)
			throw new NullPointerException();
		Object hi = highlightMap.get(a);
		if (hi != null) {
			hilit.removeHighlight(hi);
		}
		try {
			hi = hilit.addHighlight(a.getBegin(), a.getEnd(), painter);
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
		highlight(a, new Color(255, 255, 150));
	}

	public void highlight(Annotation a, Color c) {
		highlight(a, c, false, false, new DefaultHighlighter.DefaultHighlightPainter(c));
	}

	public void underline(Annotation a) {
		if (a instanceof DetachedMentionPart)
			underline((DetachedMentionPart) a);
	}

	public void underline(DetachedMentionPart dmp) {
		hilit.setDrawsLayeredHighlights(true);
		underline(dmp, new Color(dmp.getMention().getEntity().getColor()), true, true);
		hilit.setDrawsLayeredHighlights(false);
	}

	public void underline(Mention m) {
		hilit.setDrawsLayeredHighlights(true);
		Color color = new Color(m.getEntity().getColor());
		boolean dotted = false;
		if (Annotator.app.getPreferences().getBoolean(Constants.CFG_UNDERLINE_SINGLETONS_IN_GRAY,
				Defaults.CFG_UNDERLINE_SINGLETONS_IN_GRAY)) {
			if (documentModel != null && documentModel.getCoreferenceModel().getMentions(m.getEntity()).size() == 1) {
				color = Color.LIGHT_GRAY;
				dotted = false;
			}
		}
		for (MentionSurface ms : m.getSurface())
			underline(ms, color, dotted, true);
		if (m.getDiscontinuous() != null)
			underline(m.getDiscontinuous(), new Color(m.getEntity().getColor()), true, true);
		hilit.setDrawsLayeredHighlights(false);
	}

	public void underline(Annotation m, Color color) {
		hilit.setDrawsLayeredHighlights(true);
		underline(m, color, false, true);
		hilit.setDrawsLayeredHighlights(false);
	}

	public void underline(Mention m, Color color) {
		hilit.setDrawsLayeredHighlights(true);
		for (MentionSurface ms : m.getSurface())
			underline(ms, color, false, false);
		if (m.getDiscontinuous() != null)
			underline(m.getDiscontinuous(), color, true, true);
		hilit.setDrawsLayeredHighlights(false);
	}

	public void underline(Mention m, boolean repaint) {
		hilit.setDrawsLayeredHighlights(true);
		for (MentionSurface ms : m.getSurface())
			underline(ms, new Color(m.getEntity().getColor()), false, false);
		if (m.getDiscontinuous() != null)
			underline(m.getDiscontinuous(), new Color(m.getEntity().getColor()), true, false);
		hilit.setDrawsLayeredHighlights(false);
	}

	public void unUnderline(Annotation a) {
		Object hi = underlineMap.get(a);
		Span span = new Span(a);
		if (span != null)
			spanCounter.subtract(span, hi);
		if (hi != null)
			hilit.removeHighlight(hi);

	}

	public void unHighlight() {
		highlightMap.values().forEach(o -> hilit.removeHighlight(o));
	}

	public void unHighlight(Annotation a) {
		Object hi = highlightMap.get(a);
		if (hi != null)
			hilit.removeHighlight(hi);

	}

	@Deprecated
	public void undraw(Annotation a) {
		Object hi = underlineMap.get(a);
		Span span = new Span(a);
		if (span != null)
			spanCounter.subtract(span, hi);
		if (hi != null)
			hilit.removeHighlight(hi);
	}

	public DocumentModel getDocumentModel() {
		return documentModel;
	}

	public void setDocumentModel(DocumentModel documentModel) {
		this.documentModel = documentModel;
	}

}