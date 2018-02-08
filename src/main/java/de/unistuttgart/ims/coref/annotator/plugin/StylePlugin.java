package de.unistuttgart.ims.coref.annotator.plugin;

import java.util.Map;

import javax.swing.text.Style;
import javax.swing.text.StyleContext;

import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;

public interface StylePlugin {
	String getName();

	Style getBaseStyle();

	Map<Style, Type> getSpanStyles(TypeSystem typeSystem, StyleContext context, Style defaultStyle);

}
