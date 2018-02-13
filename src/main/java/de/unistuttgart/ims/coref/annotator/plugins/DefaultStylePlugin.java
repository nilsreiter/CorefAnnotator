package de.unistuttgart.ims.coref.annotator.plugins;

import java.util.Map;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleContext;

import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;

import de.unistuttgart.ims.coref.annotator.StyleManager;

public class DefaultStylePlugin implements StylePlugin {

	@Override
	public String getName() {
		return "Default";
	}

	@Override
	public MutableAttributeSet getBaseStyle() {
		return StyleManager.getDefaultParagraphStyle();
	}

	@Override
	public Map<AttributeSet, Type> getSpanStyles(TypeSystem typeSystem, StyleContext context,
			AttributeSet defaultStyle) {
		return null;
	}

	@Override
	public String getDescription() {
		return "Default";
	}

}
