package de.unistuttgart.ims.coref.annotator.plugin.tei;

import java.util.HashMap;
import java.util.Map;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;

import de.unistuttgart.ims.coref.annotator.StyleManager;
import de.unistuttgart.ims.coref.annotator.api.format.Bold;
import de.unistuttgart.ims.coref.annotator.api.format.Head;
import de.unistuttgart.ims.coref.annotator.api.format.Italic;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;

public class TeiStylePlugin implements StylePlugin {

	@Override
	public String getDescription() {
		return "Style information taken from TEI/XML";
	}

	@Override
	public String getName() {
		return "TEI/XML";
	}

	@Override
	public MutableAttributeSet getBaseStyle() {
		return StyleManager.getDefaultCharacterStyle();
	}

	@Override
	public Map<AttributeSet, Type> getSpanStyles(TypeSystem typeSystem, StyleContext context,
			AttributeSet defaultStyle) {
		Map<AttributeSet, Type> map = new HashMap<AttributeSet, Type>();

		SimpleAttributeSet sas;

		sas = new SimpleAttributeSet();
		sas.addAttribute(StyleConstants.Bold, true);
		map.put(sas, typeSystem.getType(Bold.class.getName()));

		sas = new SimpleAttributeSet();
		sas.addAttribute(StyleConstants.Italic, true);
		map.put(sas, typeSystem.getType(Italic.class.getName()));

		sas = new SimpleAttributeSet();
		sas.addAttribute(StyleConstants.FontSize, (Integer) defaultStyle.getAttribute(StyleConstants.FontSize) + 6);
		sas.addAttribute(StyleConstants.Bold, true);
		map.put(sas, typeSystem.getType(Head.class.getCanonicalName()));

		return map;
	}

}
