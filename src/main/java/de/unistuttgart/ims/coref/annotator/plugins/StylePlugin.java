package de.unistuttgart.ims.coref.annotator.plugins;

import java.util.Map;

import javax.swing.text.Style;
import javax.swing.text.StyleContext;

import org.apache.uima.jcas.tcas.Annotation;

public interface StylePlugin {
	String getName();

	Map<Style, Class<? extends Annotation>> getStyles(StyleContext context, Style defaultStyle);

}
