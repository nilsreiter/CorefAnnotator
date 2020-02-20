package de.unistuttgart.ims.coref.annotator.plugin.xlsx;

public class Constants {
	public static final String PLUGIN_XLSX_CONTEXT_WIDTH = "PLUGIN_XLSX_CONTEXT_WIDTH";
	public static final String PLUGIN_XLSX_CONTEXT_UNIT = "PLUGIN_XLSX_CONTEXT_UNIT";
	public static final String PLUGIN_XLSX_TRIM = "PLUGIN_XLSX_TRIM";
	public static final String PLUGIN_XLSX_REPLACE_NEWLINES = "PLUGIN_XLSX_REPLACE_NEWLINES";
	public static final String PLUGIN_XLSX_INCLUDE_LINE_NUMBERS = "PLUGIN_XLSX_INCLUDE_LINE_NUMBERS";
	public static final String PLUGIN_XLSX_SEPARATE_ENTITIES = "PLUGIN_XLSX_SEPARATE_ENTITIES";
	public static final String PLUGIN_XLSX_AUTO_OPEN = "PLUGIN_XLSX_AUTO_OPEN";

	@Deprecated
	public static String full(String s) {
		return Constants.class.getPackageName() + "." + s;
	}
}
