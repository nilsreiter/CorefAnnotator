package de.unistuttgart.ims.coref.annotator.plugin.csv;

public class Constants {
	public static final String PLUGIN_CSV_CONTEXT_WIDTH = "PLUGIN_CSV_CONTEXT_WIDTH";
	public static final String PLUGIN_CSV_CONTEXT_UNIT = "PLUGIN_CSV_CONTEXT_UNIT";
	public static final String PLUGIN_CSV_TRIM = "PLUGIN_CSV_TRIM";
	public static final String PLUGIN_CSV_REPLACE_NEWLINES = "PLUGIN_CSV_REPLACE_NEWLINES";
	public static final String PLUGIN_CSV_INCLUDE_LINE_NUMBERS = "PLUGIN_CSV_INCLUDE_LINE_NUMBERS";

	@Deprecated
	public static String full(String s) {
		return Constants.class.getPackageName() + "." + s;
	}
}
