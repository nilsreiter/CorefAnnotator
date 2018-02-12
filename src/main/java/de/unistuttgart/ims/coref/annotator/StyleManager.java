package de.unistuttgart.ims.coref.annotator;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

public class StyleManager {

	static Style defaultCharacterStyle = null;
	static Style defaultParagraphStyle = null;

	public static Style getDefaultCharacterStyle() {
		if (defaultCharacterStyle == null) {

			defaultCharacterStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
			defaultCharacterStyle.addAttribute(StyleConstants.FontFamily, "monospace");
		}
		return defaultCharacterStyle;
	}

	public static Style getDefaultParagraphStyle() {
		if (defaultParagraphStyle == null) {
			defaultParagraphStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
			defaultParagraphStyle.addAttribute(StyleConstants.LineSpacing, 0.5f);
		}
		return defaultParagraphStyle;
	}

	public static void revertAll(StyledDocument document) {
		styleParagraph(document, getDefaultParagraphStyle());
		styleCharacter(document, getDefaultCharacterStyle());
	}

	public static void styleCharacter(StyledDocument document, Style style) {
		document.setCharacterAttributes(0, document.getLength(), style, true);
	}

	public static void styleParagraph(StyledDocument document, Style style) {
		document.setParagraphAttributes(0, document.getLength(), style, true);
	}

	public static void style(JCas jcas, StyledDocument document, Style style, Type anno) {
		for (Annotation a : jcas.getAnnotationIndex(anno))
			document.setCharacterAttributes(a.getBegin(), a.getEnd() - a.getBegin(), style, false);
	}

}
