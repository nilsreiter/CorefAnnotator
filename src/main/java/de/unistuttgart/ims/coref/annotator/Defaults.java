package de.unistuttgart.ims.coref.annotator;

public class Defaults {
	public static final String CFG_ANNOTATOR_ID = "Unknown annotator";
	public static final int CFG_AUTOSAVE_TIMER = 300000;
	public static final boolean CFG_COMPARE_BY_ENTITY_NAME = true;
	@Deprecated
	public static final boolean CFG_CREATE_DEFAULT_FLAGS = false;
	public static final boolean CFG_DELETE_EMPTY_ENTITIES = false;
	public static final EntitySortOrder CFG_ENTITY_SORT_ORDER = EntitySortOrder.Mentions;
	public static final boolean CFG_ENTITY_SORT_DESCENDING = true;
	public static final boolean CFG_FULL_TOKENS = true;
	public static final boolean CFG_KEEP_TREE_SORTED = true;
	public static final int CFG_SEARCH_RESULTS_CONTEXT = 50;
	public static final boolean CFG_SHOW_TEXT_LABELS = true;
	public static final boolean CFG_SHOW_TOC = true;
	public static final boolean CFG_SHOW_LINE_NUMBER_IN_TREE = true;
	public static final boolean CFG_TRIM_WHITESPACE = true;
	public static final boolean CFG_ASK_BEFORE_FILE_OVERWRITE = true;
	public static final boolean CFG_IGNORE_SINGLETONS_WHEN_COMPARING = true;
	public static final boolean CFG_UNDERLINE_SINGLETONS_IN_GRAY = false;
	public static final boolean CFG_STICKY_FLAGS = false;
	public static final String CFG_MENTIONSURFACE_SEPARATOR = " | ";

}
