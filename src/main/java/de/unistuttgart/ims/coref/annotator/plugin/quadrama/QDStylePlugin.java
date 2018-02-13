package de.unistuttgart.ims.coref.annotator.plugin.quadrama;

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

public class QDStylePlugin implements de.unistuttgart.ims.coref.annotator.plugins.StylePlugin {

	@Override
	public Map<AttributeSet, Type> getSpanStyles(TypeSystem ts, StyleContext styleContext, AttributeSet defaultStyle) {
		Map<AttributeSet, Type> map = new HashMap<AttributeSet, Type>();

		SimpleAttributeSet sas;

		sas = new SimpleAttributeSet();
		sas.addAttribute(StyleConstants.Bold, true);
		map.put(sas, ts.getType(Constants.TYPE_SPEAKER));

		sas = new SimpleAttributeSet();
		sas.addAttribute(StyleConstants.Italic, true);
		map.put(sas, ts.getType(Constants.TYPE_STAGEDIRECTION));

		sas = new SimpleAttributeSet();
		sas.addAttribute(StyleConstants.FontSize, (Integer) defaultStyle.getAttribute(StyleConstants.FontSize) + 6);
		sas.addAttribute(StyleConstants.Bold, true);
		map.put(sas, ts.getType(Constants.TYPE_HEADING));

		return map;
	}

	@Override
	public MutableAttributeSet getBaseStyle() {
		return StyleManager.getDefaultCharacterStyle();
	}

	@Override
	public String getDescription() {
		return "Plugin to format speakers bold and stage directions in italic.";
	}

	@Override
	public String getName() {
		return "QuaDramA";
	}

}
