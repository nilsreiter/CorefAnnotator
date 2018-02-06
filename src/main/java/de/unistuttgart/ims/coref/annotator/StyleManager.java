package de.unistuttgart.ims.coref.annotator;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

public class StyleManager {

	static Style defaultStyle = null;

	public static Style getDefaultStyle() {
		if (defaultStyle == null) {
			defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
			defaultStyle.addAttribute(StyleConstants.LineSpacing, 5f);
			defaultStyle.addAttribute(StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		}
		return defaultStyle;
	}

	public static void style(JCas jcas, StyledDocument document, Style style, Class<? extends Annotation> anno) {
		for (Annotation a : JCasUtil.select(jcas, anno))
			document.setCharacterAttributes(a.getBegin(), a.getEnd() - a.getBegin(), style, false);
	}

}
