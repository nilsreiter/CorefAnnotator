package de.unistuttgart.ims.coref.annotator;

import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

public class StyleManager {

	static SimpleAttributeSet defaultCharacterStyle = null;
	static SimpleAttributeSet defaultParagraphStyle = null;

	public static MutableAttributeSet getDefaultCharacterStyle() {
		if (defaultCharacterStyle == null) {
			defaultCharacterStyle = new SimpleAttributeSet();// StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
			defaultCharacterStyle.addAttribute(StyleConstants.FontFamily, Font.DIALOG);
			defaultCharacterStyle.addAttribute(StyleConstants.FontSize, UIManager.getFont("TextPane.font").getSize());
		}
		return defaultCharacterStyle;
	}

	public static MutableAttributeSet getDefaultParagraphStyle() {
		if (defaultParagraphStyle == null) {
			defaultParagraphStyle = new SimpleAttributeSet();
			StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
			defaultParagraphStyle.addAttribute(StyleConstants.LineSpacing, 0.5f);
			defaultParagraphStyle.addAttribute(StyleConstants.FontFamily, Font.DIALOG);
			defaultParagraphStyle.addAttribute(StyleConstants.FontSize, UIManager.getFont("TextPane.font").getSize());
		}
		return defaultParagraphStyle;
	}

	public static void revertAll(StyledDocument document) {
		styleParagraph(document, getDefaultParagraphStyle());
		styleCharacter(document, getDefaultCharacterStyle());
	}

	public static void styleCharacter(StyledDocument document, AttributeSet style) {
		document.setCharacterAttributes(0, document.getLength(), style, true);
	}

	public static void styleParagraph(StyledDocument document, AttributeSet style) {
		document.setParagraphAttributes(0, document.getLength(), style, true);
	}

	public static void style(JCas jcas, StyledDocument document, AttributeSet style, Type anno) {
		for (Annotation a : jcas.getAnnotationIndex(anno))
			document.setCharacterAttributes(a.getBegin(), a.getEnd() - a.getBegin(), style, false);
	}

}
