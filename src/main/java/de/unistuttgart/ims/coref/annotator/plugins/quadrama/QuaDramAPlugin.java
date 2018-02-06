package de.unistuttgart.ims.coref.annotator.plugins.quadrama;

import java.util.HashMap;
import java.util.Map;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;

public class QuaDramAPlugin implements IOPlugin, StylePlugin {

	private static final String TYPE_SPEAKER = "de.unistuttgart.ims.drama.api.Speaker";
	private static final String TYPE_STAGEDIRECTION = "de.unistuttgart.ims.drama.api.StageDirection";
	private static final String TYPE_HEADING = "de.unistuttgart.ims.drama.api.Heading";

	@Override
	public String getDescription() {
		return "Plugin that provides an importer and style.";
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
	public Map<Style, Type> getSpanStyles(TypeSystem ts, StyleContext styleContext, Style defaultStyle) {
		Map<Style, Type> map = new HashMap<Style, Type>();

		Style style = styleContext.addStyle("Speaker", defaultStyle);
		style.addAttribute(StyleConstants.Bold, true);
		map.put(style, ts.getType(TYPE_SPEAKER));

		style = styleContext.addStyle("Stage direction", defaultStyle);
		style.addAttribute(StyleConstants.Italic, true);
		map.put(style, ts.getType(TYPE_STAGEDIRECTION));

		style = styleContext.addStyle("Header", defaultStyle);
		style.addAttribute(StyleConstants.FontSize, 16);
		map.put(style, ts.getType(TYPE_HEADING));

		return map;
	}

	@Override
	public StylePlugin getStylePlugin() {
		return this;
	}

	@Override
	public Style getBaseStyle() {
		return null;
	}

}
