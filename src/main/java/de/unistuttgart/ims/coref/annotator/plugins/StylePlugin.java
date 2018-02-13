package de.unistuttgart.ims.coref.annotator.plugins;

import java.util.Map;

import javax.swing.text.AttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;

import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;

public interface StylePlugin extends Plugin {
	Style getBaseStyle();

	Map<AttributeSet, Type> getSpanStyles(TypeSystem typeSystem, StyleContext context, Style defaultStyle);

}
