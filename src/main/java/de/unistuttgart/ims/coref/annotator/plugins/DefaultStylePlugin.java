package de.unistuttgart.ims.coref.annotator.plugins;

import java.util.Map;

import javax.swing.text.Style;
import javax.swing.text.StyleContext;

import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;

public class DefaultStylePlugin implements StylePlugin {

	@Override
	public String getName() {
		return "Default";
	}

	@Override
	public Style getBaseStyle() {
		return null;
	}

	@Override
	public Map<Style, Type> getSpanStyles(TypeSystem typeSystem, StyleContext context, Style defaultStyle) {
		return null;
	}

}