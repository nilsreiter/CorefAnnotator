package de.unistuttgart.ims.coref.annotator;

import java.util.Random;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.document.op.AddFlag;

public class Constants {
	public static class Setting<T> {

		public T defaultValue;
		public Ikon[] ikon;
		public String preferencesKey;
		public String toggleActionStringKey;
		public String toggleActionTooltipKey;

		public Setting(String preferencesKey, String toggleActionKey, String toggleActionTooltipKey, T defaultValue,
				Ikon... ikon) {
			this.preferencesKey = preferencesKey;
			this.toggleActionStringKey = toggleActionKey;
			this.defaultValue = defaultValue;
			this.toggleActionTooltipKey = toggleActionTooltipKey;
			this.ikon = ikon;
		}

		public Setting(String preferencesKey, String toggleActionKey, T defaultValue, Ikon... ikon) {
			this.preferencesKey = preferencesKey;
			this.toggleActionStringKey = toggleActionKey;
			this.defaultValue = defaultValue;
			this.ikon = ikon;
		}

		public T getDefaultValue() {
			return defaultValue;
		}

		public Ikon[] getIkon() {
			return ikon;
		}

		public String getPreferencesKey() {
			return preferencesKey;
		}

		public String getToggleActionStringKey() {
			return toggleActionStringKey;
		}

		public String getToggleActionTooltipKey() {
			return toggleActionTooltipKey;
		}

		public void setDefaultValue(T defaultValue) {
			this.defaultValue = defaultValue;
		}

		public void setIkon(Ikon[] ikon) {
			this.ikon = ikon;
		}

		public void setPreferencesKey(String preferencesKey) {
			this.preferencesKey = preferencesKey;
		}

		public void setToggleActionStringKey(String toggleActionStringKey) {
			this.toggleActionStringKey = toggleActionStringKey;
		}

		public void setToggleActionTooltipKey(String toggleActionTooltipKey) {
			this.toggleActionTooltipKey = toggleActionTooltipKey;
		}
	}

	/**
	 * Annotator name or id
	 */
	public static final String CFG_ANNOTATOR_ID = "CFG_ANNOTATOR_ID";

	/**
	 * If enabled, the tool asks whether to overwrite the file if it exists. This
	 * does only apply to the "Save as..." action.
	 */
	public static final String CFG_ASK_BEFORE_FILE_OVERWRITE = "CFG_ASK_FILE_OVERWRITE";

	/**
	 * The interval in which the file is auto-saved (in milliseconds). 1 minute =
	 * 60000 milliseconds.
	 * 
	 * @since 1.14
	 */
	public static final String CFG_AUTOSAVE_TIMER = "CFG_AUTOSAVE_TIMER";

	/**
	 * What the tool considers the current directory.
	 */
	public static final String CFG_CURRENT_DIRECTORY = "CFG_CURRENT_DIRECTORY";

	/**
	 * Controls whether the compare view uses annotator names for colors or entity
	 * names. The latter allows comparing annotation categories.
	 * 
	 * @since 1.13
	 */
	public static final String CFG_COMPARE_BY_ENTITY_NAME = "CFG_COMPARE_BY_ENTITY_NAME";

	/**
	 * If enabled, each document gets a few default flags for mentions and entities.
	 * Should be disabled for non-coreference annotations.
	 * 
	 * @since 1.13
	 */
	@Deprecated
	public static final String CFG_CREATE_DEFAULT_FLAGS = "CFG_CREATE_DEFAULT_FLAGS";

	/**
	 * 
	 */
	public static final String CFG_DELETE_EMPTY_ENTITIES = "CFG_DELETE_EMPTY_ENTITIES";

	/**
	 * If enabled, selecting a single character within a token expands to the full
	 * token (up to the next boundary character \b.
	 */
	public static final String CFG_FULL_TOKENS = "full_tokens";

	/**
	 * In the compare view, singletons are ignored (i.e., not shown and not included
	 * in calculation).
	 */
	public static final String CFG_IGNORE_SINGLETONS_WHEN_COMPARING = "CFG_IGNORE_SINGLETONS_WHEN_COMPARING";

	/**
	 * If enabled, automatically resorts the entity tree when necessary.
	 */
	public static final String CFG_KEEP_TREE_SORTED = "CFG_KEEP_TREE_SORTED";

	/**
	 * If enabled, assigning a new mention on top of an existing one replaces the
	 * existing one.
	 * 
	 * @since 1.10
	 */
	public static final String CFG_REPLACE_MENTION = "CFG_REPLACE_MENTION";

	/**
	 * The context window of the search results, measured in characters.
	 */
	public static final String CFG_SEARCH_RESULTS_CONTEXT = "CFG_SEARCH_RESULTS_CONTEXT";

	/**
	 * If enabled, markers are shown with textual descriptions in the tree view.
	 */
	public static final String CFG_SHOW_TEXT_LABELS = "Show text labels";

	/**
	 * If enabled, there is a table of contents shown left to the text.
	 */
	public static final String CFG_SHOW_TOC = "CFG_SHOW_TOC";

	/**
	 * Whether to display a line number in the tree view.
	 */
	public static final String CFG_SHOW_LINE_NUMBER_IN_TREE = "CFG_SHOW_LINE_NUMBER_IN_TREE";

	/**
	 * If enabled, mentions with the same boundaries are considered the same w.r.t.
	 * flag assignments. I.e., it doesn't matter to which mention a flag is
	 * assigned, all mentions with the same span get it.
	 * 
	 * @since 1.14
	 */
	public static final String CFG_STICKY_FLAGS = "CFG_STICKY_FLAGS";

	/**
	 * Whether to remove whitespace left and right of an annotation.
	 */
	public static final String CFG_TRIM_WHITESPACE = "Trim Whitespace";

	/**
	 * By what to sort the entity tree
	 */
	public static final String CFG_ENTITY_SORT_ORDER = "CFG_ENTITY_SORT_ORDER";

	/**
	 * Sort order
	 */
	public static final String CFG_ENTITY_SORT_DESCENDING = "CFG_ENTITY_SORT_DESCENDING";

	/**
	 * 
	 */
	public static final String CFG_UNDERLINE_SINGLETONS_IN_GRAY = "CFG_UNDERLINE_SINGLETONS_IN_GRAY";

	@Deprecated
	public static final String ENTITY_FLAG_GENERIC = "Generic";
	@Deprecated
	public static final String ENTITY_FLAG_HIDDEN = "Hidden";

	public static final int MAX_SEGMENTS_IN_SCROLLBAR = 15;

	public static final AddFlag[] FLAG_COLLECTION_1 = new AddFlag[] {
			new AddFlag("female", "Female", MaterialDesign.MDI_GENDER_FEMALE, Entity.class),
			new AddFlag("male", "Male", MaterialDesign.MDI_GENDER_MALE, Entity.class),
			new AddFlag("male-female", "Male/Female", MaterialDesign.MDI_GENDER_MALE_FEMALE, Entity.class),
			new AddFlag("transgender", "Transgender", MaterialDesign.MDI_GENDER_TRANSGENDER, Entity.class) };

	public static final AddFlag[] FLAG_COLLECTION_2 = new AddFlag[] {
			new AddFlag("predicate", "Predicate", MaterialDesign.MDI_GAVEL, Mention.class),
			new AddFlag("pronoun", "Pronoun", MaterialDesign.MDI_ARROW_TOP_LEFT, Mention.class),
			new AddFlag("name", "Proper name", MaterialDesign.MDI_TAG, Mention.class) };

	@Deprecated
	public static final String MENTION_FLAG_AMBIGUOUS = "Ambiguous";

	@Deprecated
	public static final String MENTION_FLAG_DIFFICULT = "Difficult";

	@Deprecated
	public static final String MENTION_FLAG_NON_NOMINAL = "Non Nominal";

	public static final String PREF_RECENT = "recent_files";

	public static final Random RANDOM = new Random();

	public static final Setting<Boolean> SETTING_ASK_BEFORE_FILE_OVERWRITE = new Setting<Boolean>(
			CFG_ASK_BEFORE_FILE_OVERWRITE, Strings.ACTION_TOGGLE_ASK_BEFORE_FILE_OVERWRITE,
			Strings.ACTION_TOGGLE_ASK_BEFORE_FILE_OVERWRITE, Defaults.CFG_ASK_BEFORE_FILE_OVERWRITE,
			MaterialDesign.MDI_SETTINGS);

	public static final Setting<Integer> SETTING_AUTOSAVE_TIMER = new Setting<Integer>(Constants.CFG_AUTOSAVE_TIMER,
			null, Defaults.CFG_AUTOSAVE_TIMER, MaterialDesign.MDI_TIMER);

	public static final Setting<Boolean> SETTING_CREATE_DEFAULT_FLAGS = new Setting<Boolean>(CFG_CREATE_DEFAULT_FLAGS,
			Strings.ACTION_TOGGLE_CREATE_DEFAULT_FLAGS, Strings.ACTION_TOGGLE_CREATE_DEFAULT_FLAGS_TOOLTIP, false,
			MaterialDesign.MDI_FLAG_OUTLINE);

	public static final Setting<Boolean> SETTING_DELETE_EMPTY_ENTITIES = new Setting<Boolean>(CFG_DELETE_EMPTY_ENTITIES,
			Strings.ACTION_TOGGLE_DELETE_EMPTY_ENTITIES, Strings.ACTION_TOGGLE_DELETE_EMPTY_ENTITIES, true,
			MaterialDesign.MDI_GHOST);

	public static final Setting<Boolean> SETTING_FULL_TOKENS = new Setting<Boolean>(CFG_FULL_TOKENS,
			Strings.ACTION_TOGGLE_FULL_TOKENS, Strings.ACTION_TOGGLE_FULL_TOKENS_TOOLTIP, true,
			MaterialDesign.MDI_VIEW_WEEK);

	public static final Setting<Boolean> SETTING_IGNORE_SINGLETONS_WHEN_COMPARING = new Setting<Boolean>(
			CFG_IGNORE_SINGLETONS_WHEN_COMPARING, Strings.ACTION_TOGGLE_IGNORE_SINGLETONS_WHEN_COMPARING,
			Strings.ACTION_TOGGLE_IGNORE_SINGLETONS_WHEN_COMPARING_TOOLTIP, true, MaterialDesign.MDI_SETTINGS);

	public static final Setting<Boolean> SETTING_KEEP_TREE_SORTED = new Setting<Boolean>(CFG_KEEP_TREE_SORTED,
			Strings.ACTION_TOGGLE_KEEP_TREE_SORTED, Strings.ACTION_TOGGLE_TRIM_WHITESPACE_TOOLTIP, true,
			MaterialDesign.MDI_SORT_VARIANT);

	public static final Setting<Boolean> SETTING_SHOW_TEXT_LABELS = new Setting<Boolean>(CFG_SHOW_TEXT_LABELS,
			Strings.ACTION_TOGGLE_SHOW_TEXT_LABELS, Strings.ACTION_TOGGLE_SHOW_TEXT_LABELS_TOOLTIP, false,
			MaterialDesign.MDI_FORMAT_TEXT);
	public static final Setting<Boolean> SETTING_SHOW_LINE_NUMBER_IN_TREE = new Setting<Boolean>(
			CFG_SHOW_LINE_NUMBER_IN_TREE, Strings.ACTION_TOGGLE_SHOW_LINE_NUMBER_IN_TREE,
			Strings.ACTION_TOGGLE_SHOW_LINE_NUMBER_IN_TREE_TOOLTIP, false, MaterialDesign.MDI_FORMAT_TEXT);
	public static final Setting<Boolean> SETTING_TRIM_WHITESPACE = new Setting<Boolean>(CFG_TRIM_WHITESPACE,
			Strings.ACTION_TOGGLE_TRIM_WHITESPACE, Strings.ACTION_TOGGLE_TRIM_WHITESPACE_TOOLTIP, true,
			MaterialDesign.MDI_ARROW_COMPRESS);

	public static final Setting<Boolean> SETTING_REPLACE_MENTION = new Setting<Boolean>(CFG_REPLACE_MENTION,
			Strings.ACTION_TOGGLE_REPLACE_MENTION, Strings.ACTION_TOGGLE_REPLACE_MENTION_TOOLTIP, false,
			MaterialDesign.MDI_DIRECTIONS_FORK);

	public static final Setting<Boolean> SETTING_UNDERLINE_SINGLETONS_IN_GRAY = new Setting<Boolean>(
			CFG_UNDERLINE_SINGLETONS_IN_GRAY, Strings.ACTION_TOGGLE_UNDERLINE_SINGLETONS_IN_GRAY,
			Strings.ACTION_TOGGLE_UNDERLINE_SINGLETONS_IN_GRAY_TOOLTIP, false, MaterialDesign.MDI_FORMAT_UNDERLINE);

	public static final Setting<Boolean> SETTING_COMPARE_BY_ENTITY_NAME = new Setting<Boolean>(
			CFG_COMPARE_BY_ENTITY_NAME, Strings.ACTION_TOGGLE_COMPARE_BY_ENTITY_NAME,
			Strings.ACTION_TOGGLE_COMPARE_BY_ENTITY_NAME_TOOLTIP, false, MaterialDesign.MDI_FORMAT_UNDERLINE);

	public static final Setting<Boolean> SETTING_SHOW_TOC = new Setting<Boolean>(CFG_SHOW_TOC,
			Strings.ACTION_TOGGLE_SHOW_TOC, Strings.ACTION_TOGGLE_SHOW_TOC_TOOLTIP, Defaults.CFG_SHOW_TOC,
			MaterialDesign.MDI_FORMAT_LIST_BULLETED_TYPE);

	public static final Setting<Boolean> SETTING_STICKY_FLAGS = new Setting<Boolean>(CFG_STICKY_FLAGS,
			Strings.ACTION_TOGGLE_STICKY_FLAGS, Strings.ACTION_TOGGLE_STICKY_FLAGS_TOOLTIP, Defaults.CFG_STICKY_FLAGS,
			MaterialDesign.MDI_FORMAT_LIST_BULLETED_TYPE);

	public static final String[] SUPPORTED_LANGUAGES = new String[] { "x-unspecified", "de", "en", "es", "fr", "it",
			"nl", "ru", "gmh" };

	public static final int UI_MAX_STRING_WIDTH_IN_TREE = 50;
	public static final int UI_MAX_STRING_WIDTH_IN_MENU = 30;
	public static final int UI_MAX_STRING_WIDTH_IN_STATUSBAR = 30;

	public static final String URL_LATEST_RELEASE_API = "https://api.github.com/repos/nilsreiter/CorefAnnotator/releases/latest";

	public static final String X_UNSPECIFIED = "x-unspecified";
}
