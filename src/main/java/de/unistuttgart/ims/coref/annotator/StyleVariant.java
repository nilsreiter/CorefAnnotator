package de.unistuttgart.ims.coref.annotator;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.creta.api.Stage;
import de.unistuttgart.ims.drama.api.Heading;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.StageDirection;

public enum StyleVariant {
	Default, QuaDramA, CRETA_Bundestag;

	public static StyleVariant select(CoreferenceFlavor flavor) {
		switch (flavor) {
		case QuaDramA:
			return QuaDramA;
		default:
			return Default;
		}
	}

	public void style(JCas jcas, StyledDocument document, StyleContext styleContext) {
		Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		document.setCharacterAttributes(0, document.getLength(), defaultStyle, true);

		switch (this) {
		case QuaDramA:
			styleQuaDramA(jcas, document, styleContext);
			break;
		case CRETA_Bundestag:
			styleCRETABundestag(jcas, document, styleContext);
			break;
		default:
			break;
		}
	}

	protected void styleCRETABundestag(JCas jcas, StyledDocument document, StyleContext styleContext) {
		Style style = styleContext.addStyle("Stage", null);
		style.addAttribute(StyleConstants.Italic, true);
		style(jcas, document, style, Stage.class);
	}

	protected void styleQuaDramA(JCas jcas, StyledDocument document, StyleContext styleContext) {

		Style style = styleContext.addStyle("Speaker", null);
		style.addAttribute(StyleConstants.Bold, true);
		style(jcas, document, style, Speaker.class);

		style = styleContext.addStyle("Stage direction", null);
		style.addAttribute(StyleConstants.Italic, true);
		style(jcas, document, style, StageDirection.class);

		style = styleContext.addStyle("Header", null);
		style.addAttribute(StyleConstants.FontSize, 16);
		style(jcas, document, style, Heading.class);

		return;
	}

	private void style(JCas jcas, StyledDocument document, Style style, Class<? extends Annotation> anno) {
		for (Annotation a : JCasUtil.select(jcas, anno))
			document.setCharacterAttributes(a.getBegin(), a.getEnd() - a.getBegin(), style, false);
	}

}
