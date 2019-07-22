package de.unistuttgart.ims.coref.annotator;

import java.util.Random;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
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

	public static final String CFG_ANNOTATOR_ID = "CFG_ANNOTATOR_ID";
	public static final String CFG_ASK_BEFORE_FILE_OVERWRITE = "CFG_ASK_FILE_OVERWRITE";
	public static final String CFG_CURRENT_DIRECTORY = "CFG_CURRENT_DIRECTORY";

	public static final String CFG_DELETE_EMPTY_ENTITIES = "CFG_DELETE_EMPTY_ENTITIES";

	public static final String CFG_FULL_TOKENS = "full_tokens";
	public static final String CFG_IGNORE_SINGLETONS_WHEN_COMPARING = "CFG_IGNORE_SINGLETONS_WHEN_COMPARING";
	public static final String CFG_KEEP_TREE_SORTED = "CFG_KEEP_TREE_SORTED";
	public static final String CFG_REPLACE_MENTION = "CFG_REPLACE_MENTION";
	public static final String CFG_SEARCH_RESULTS_CONTEXT = "CFG_SEARCH_RESULTS_CONTEXT";
	public static final String CFG_SHOW_TEXT_LABELS = "Show text labels";
	public static final String CFG_TRIM_WHITESPACE = "Trim Whitespace";

	public static final String ENTITY_FLAG_GENERIC = "Generic";
	public static final String ENTITY_FLAG_HIDDEN = "Hidden";

	public static final AddFlag[] FLAG_COLLECTION_1 = new AddFlag[] {
			new AddFlag("female", "Female", MaterialDesign.MDI_GENDER_FEMALE, Entity.class),
			new AddFlag("male", "Male", MaterialDesign.MDI_GENDER_MALE, Entity.class),
			new AddFlag("male-female", "Male/Female", MaterialDesign.MDI_GENDER_MALE_FEMALE, Entity.class),
			new AddFlag("transgender", "Transgender", MaterialDesign.MDI_GENDER_TRANSGENDER, Entity.class) };

	public static final AddFlag[] FLAG_COLLECTION_2 = new AddFlag[] {
			new AddFlag("predicate", "Predicate", MaterialDesign.MDI_GAVEL, Mention.class),
			new AddFlag("pronoun", "Pronoun", MaterialDesign.MDI_ARROW_TOP_LEFT, Mention.class),
			new AddFlag("name", "Proper name", MaterialDesign.MDI_TAG, Mention.class) };

	public static final String MENTION_FLAG_AMBIGUOUS = "Ambiguous";

	public static final String MENTION_FLAG_DIFFICULT = "Difficult";

	public static final String MENTION_FLAG_NON_NOMINAL = "Non Nominal";

	public static final String PREF_RECENT = "recent_files";

	public static final Random RANDOM = new Random();

	public static final Setting<Boolean> SETTING_ASK_BEFORE_FILE_OVERWRITE = new Setting<Boolean>(
			CFG_ASK_BEFORE_FILE_OVERWRITE, Strings.ACTION_TOGGLE_ASK_BEFORE_FILE_OVERWRITE,
			Strings.ACTION_TOGGLE_ASK_BEFORE_FILE_OVERWRITE, Defaults.CFG_ASK_BEFORE_FILE_OVERWRITE,
			MaterialDesign.MDI_SETTINGS);

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
	public static final Setting<Boolean> SETTING_TRIM_WHITESPACE = new Setting<Boolean>(CFG_TRIM_WHITESPACE,
			Strings.ACTION_TOGGLE_TRIM_WHITESPACE, Strings.ACTION_TOGGLE_TRIM_WHITESPACE_TOOLTIP, true,
			MaterialDesign.MDI_ARROW_COMPRESS);

	public static final Setting<Boolean> SETTING_REPLACE_MENTION = new Setting<Boolean>(CFG_REPLACE_MENTION,
			Strings.ACTION_TOGGLE_REPLACE_MENTION, Strings.ACTION_TOGGLE_REPLACE_MENTION_TOOLTIP, false,
			MaterialDesign.MDI_DIRECTIONS_FORK);

	public static final String[] SUPPORTED_LANGUAGES = new String[] { "x-unspecified", "de", "en", "es", "fr", "it",
			"nl", "ru", "gmh" };

	public static final int UI_MAX_STRING_WIDTH_IN_TREE = 50;

	public static final String URL_LATEST_RELEASE_API = "https://api.github.com/repos/nilsreiter/CorefAnnotator/releases/latest";

	public static final String X_UNSPECIFIED = "x-unspecified";
}
