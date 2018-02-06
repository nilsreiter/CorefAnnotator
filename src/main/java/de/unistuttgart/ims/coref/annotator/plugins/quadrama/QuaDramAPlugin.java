package de.unistuttgart.ims.coref.annotator.plugins.quadrama;

import java.util.HashMap;
import java.util.Map;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;
import de.unistuttgart.ims.drama.api.Heading;
import de.unistuttgart.ims.drama.api.Speaker;
import de.unistuttgart.ims.drama.api.StageDirection;

public class QuaDramAPlugin implements IOPlugin, StylePlugin {

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "QuaDramA";
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(ImportQuaDramA.class);
	}

	@Override
	public AnalysisEngineDescription getExporter() {
		return null;
	}

	@Override
	public Map<Style, Class<? extends Annotation>> getStyles(StyleContext styleContext, Style defaultStyle) {
		Map<Style, Class<? extends Annotation>> map = new HashMap<Style, Class<? extends Annotation>>();

		Style style = styleContext.addStyle("Speaker", defaultStyle);
		style.addAttribute(StyleConstants.Bold, true);
		map.put(style, Speaker.class);

		style = styleContext.addStyle("Stage direction", defaultStyle);
		style.addAttribute(StyleConstants.Italic, true);
		map.put(style, StageDirection.class);

		style = styleContext.addStyle("Header", defaultStyle);
		style.addAttribute(StyleConstants.FontSize, 16);
		map.put(style, Heading.class);

		return map;
	}

	@Override
	public StylePlugin getStylePlugin() {
		return this;
	}

}
