package de.unistuttgart.ims.coref.annotator;

import javafx.stage.FileChooser.ExtensionFilter;

public class ExtensionFilters {
	static public ExtensionFilter xmi_gz = new ExtensionFilter("UIMA Xmi Files (Compressed/Uncompressed)", "*.xmi",
			"*.xmi.gz");
	static public ExtensionFilter xmi = new ExtensionFilter("UIMA Xmi Files", "*.xmi");
	static public ExtensionFilter txt = new ExtensionFilter("Plain text", "*.txt");
	static public ExtensionFilter tei = new ExtensionFilter("TEI", "*.xml", "*.tei");
	static public ExtensionFilter json = new ExtensionFilter("JSON", "*.json");
	static public ExtensionFilter csv = new ExtensionFilter("CSV", "*.csv");
	static public ExtensionFilter ca2 = new ExtensionFilter("CorefAnnotator files", "*.ca2");

}
