package de.unistuttgart.ims.coref.annotator;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

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

	public static class Strings {

		public static final String ACTION_ADD_FINDINGS_TO_ENTITY = "action.add_findings_to_entity";
		public static final String ACTION_ADD_FINDINGS_TO_ENTITY_TOOLTIP = "action.add_findings_to_entity.tooltip";
		public static final String ACTION_ADD_FINDINGS_TO_NEW_ENTITY = "action.add_findings_to_new_entity";
		public static final String ACTION_ADD_FINDINGS_TO_NEW_ENTITY_TOOLTIP = "action.add_findings_to_new_entity.tooltip";
		public static final String ACTION_CLEAR = "action.clear";
		public static final String ACTION_CLOSE = "action.close";
		public static final String ACTION_COMMENT = "action.comment";
		public static final String ACTION_COMMENT_DELETE = "action.comment.delete";
		public static final String ACTION_COMMENT_DELETE_TOOLTIP = "action.comment.delete.tooltip";
		public static final String ACTION_COMMENT_REVEAL_LOCATION = "action.comment.reveal_location";
		public static final String ACTION_COMMENT_REVEAL_LOCATION_TOOLTIP = "action.comment.reveal_location.tooltip";
		public static final String ACTION_COMMENT_SAVE = "action.comment.save";
		public static final String ACTION_COMMENT_SAVE_TOOLTIP = "action.comment.save.tooltip";
		public static final String ACTION_COMPARE = "action.compare";
		public static final String ACTION_COMPARE_TOOLTIP = "action.compare.tooltip";
		public static final String ACTION_COPY = "action.copy";
		public static final String ACTION_DELETE = "action.delete";
		public static final String ACTION_DELETE_TOOLTIP = "action.delete.tooltip";
		public static final String ACTION_EDIT_COMMENT = "action.edit.comment";
		public static final String ACTION_EDIT_COMMENT_TOOLTIP = "action.edit.comment.tooltip";
		public static final String ACTION_ENTITY_STATISTICS = "action.entity_statistics";
		public static final String ACTION_ENTITY_STATISTICS_TOOLTIP = "action.entity_statistics.tooltip";
		public static final String ACTION_FILE_MERGE = "action.merge_files";
		public static final String ACTION_FILE_MERGE_TOOLTIP = "action.merge_files.tooltip";
		public static final String ACTION_FLAG_ENTITY_GENERIC = "action.flag_entity_generic";
		public static final String ACTION_FLAG_ENTITY_GENERIC_TOOLTIP = "action.flag_entity_generic.tooltip";
		public static final String ACTION_FLAG_MENTION_AMBIGUOUS = "action.flag_mention_ambiguous";
		public static final String ACTION_FLAG_MENTION_DIFFICULT = "action.flag_mention_difficult";
		public static final String ACTION_FLAG_MENTION_DIFFICULT_TOOLTIP = "action.flag_mention_difficult.tooltip";
		public static final String ACTION_FLAG_MENTION_NON_NOMINAL = "action.flag_mention_non_nominal";
		public static final String ACTION_FLAG_MENTION_NON_NOMINAL_TOOLTIP = "action.flag_mention_non_nominal.tooltip";
		public static final String ACTION_GROUP = "action.group";
		public static final String ACTION_GROUP_TOOLTIP = "action.group.tooltip";
		public static final String ACTION_MERGE = "action.merge";
		public static final String ACTION_MERGE_TOOLTIP = "action.merge.tooltip";
		public static final String ACTION_NEW = "action.new";
		public static final String ACTION_NEW_TOOLTIP = "action.new.tooltip";
		public static final String ACTION_OPEN = "action.open";
		public static final String ACTION_REMOVE_FOREIGN_ANNOTATIONS = "action.remove.foreign_annotations";
		public static final String ACTION_REMOVE_FOREIGN_ANNOTATIONS_TOOLTIP = "action.remove.foreign_annotations.tooltip";
		public static final String ACTION_RENAME = "action.rename";
		public static final String ACTION_RENAME_TOOLTIP = "action.rename.tooltip";
		public static final String ACTION_SAVE_AS = "action.save_as";
		public static final String ACTION_SEARCH = "action.search";
		public static final String ACTION_SET_ANNOTATOR_NAME = "action.set_annotator_name";
		public static final String ACTION_SET_COLOR = "action.set_color";
		public static final String ACTION_SET_COLOR_TOOLTIP = "action.set_color.tooltip";
		public static final String ACTION_SET_DOCUMENT_LANGUAGE = "action.set_document_language";
		public static final String ACTION_SET_DOCUMENT_LANGUAGE_TOOLTIP = "action.set_document_language.tooltip";
		public static final String ACTION_SET_SHORTCUT = "action.set_shortcut";
		public static final String ACTION_SET_SHORTCUT_TOOLTIP = "action.set_shortcut.tooltip";
		public static final String ACTION_SHOW_COMMENTS = "action.show.comments";
		public static final String ACTION_SHOW_HISTORY = "action.show.history";
		public static final String ACTION_SHOW_LOG = "action.show.log";
		public static final String ACTION_SHOW_MENTION_IN_TREE = "action.show_mention_in_tree";
		public static final String ACTION_SORT_ALPHA = "action.sort_alpha";
		public static final String ACTION_SORT_MENTIONS = "action.sort_mentions";
		public static final String ACTION_SORT_MENTIONS_TOOLTIP = "action.sort_mentions.tooltip";
		public static final String ACTION_SORT_REVERT = "action.sort_revert";
		public static final String ACTION_TOGGLE_ASK_BEFORE_FILE_OVERWRITE = "action.toggle.ask_before_file_overwrite";
		public static final String ACTION_TOGGLE_DELETE_EMPTY_ENTITIES = "action.toggle.delete_empty_entities";
		public static final String ACTION_TOGGLE_ENTITY_VISIBILITY = "action.toggle.entity_visibility";
		public static final String ACTION_TOGGLE_FULL_TOKENS = "action.toggle.full_tokens";
		public static final String ACTION_TOGGLE_FULL_TOKENS_TOOLTIP = "action.toggle.full_tokens.tooltip";
		public static final String ACTION_TOGGLE_KEEP_TREE_SORTED = "action.toggle.keep_tree_sorted";
		public static final String ACTION_TOGGLE_KEEP_TREE_SORTED_TOOLTIP = "action.toggle.keep_tree_sorted.tooltip";
		public static final String ACTION_TOGGLE_SHOW_TEXT_LABELS = "action.toggle.show_text_labels";
		public static final String ACTION_TOGGLE_SHOW_TEXT_LABELS_TOOLTIP = "action.toggle.show_text_labels.tooltip";
		public static final String ACTION_TOGGLE_TRIM_WHITESPACE = "action.toggle.trim_whitespace";
		public static final String ACTION_TOGGLE_TRIM_WHITESPACE_TOOLTIP = "action.toggle.trim_whitespace.tooltip";
		public static final String ACTION_UNDO = "action.undo";
		public static final String ACTION_VIEW_DECREASE_FONT_SIZE = "action.view.decrease_font_size";
		public static final String ACTION_VIEW_INCREASE_FONT_SIZE = "action.view.increase_font_size";
		public static final String DIALOG_ANNOTATOR_LABEL = "dialog.annotator_label";
		public static final String DIALOG_ANNOTATOR_LABEL_TOOLTIP = "dialog.annotator_label.tooltip";
		public static final String DIALOG_CANCEL = "dialog.cancel";
		public static final String DIALOG_CHANGE_ANNOTATOR_NAME_PROMPT = "dialog.change_annotator_name.prompt";
		public static final String DIALOG_CHANGE_COLOR_PROMPT = "dialog.change_color.prompt";
		public static final String DIALOG_CHANGE_KEY_INVALID_STRING_MESSAGE = "dialog.change_key.invalid_string.message";
		public static final String DIALOG_CHANGE_KEY_INVALID_STRING_TITLE = "dialog.change_key.invalid_string.title";
		public static final String DIALOG_CHANGE_KEY_PROMPT = "dialog.change_key.prompt";
		public static final String DIALOG_EXPORT_AS_TITLE = "dialog.export_as.title";
		public static final String DIALOG_FILE_EXISTS_OVERWRITE = "dialog.file_exists.overwrite";
		public static final String DIALOG_LANGUAGE_PROMPT = "dialog.language.prompt";
		public static final String DIALOG_LANGUAGE_TITLE = "dialog.language.title";
		public static final String DIALOG_RENAME_ENTITY_PROMPT = "dialog.rename_entity.prompt";
		public static final String DIALOG_SAVE_AS_TITLE = "dialog.save_as.title";
		public static final String DIALOG_SELECT_FILE = "dialog.select_file";
		public static final String DIALOG_UNSAVED_CHANGES_MESSAGE = "dialog.unsaved_changes.message";
		public static final String DIALOG_UNSAVED_CHANGES_TITLE = "dialog.unsaved_changes.title";
		public static final String ENTITY_FLAG_GENERIC = "entity.flag.generic";
		public static final String ENTITY_GROUP_AND = "entity.group.and";
		public static final String LANGUAGE = "language";
		public static final String MENTION_FLAG_AMBIGUOUS = "mention.flag.ambiguous";
		public static final String MENTION_FLAG_DIFFICULT = "mention.flag.difficult";
		public static final String MENTION_FLAG_NON_NOMINAL = "mention.flag.non_nominal";
		public static final String MENU_COMMENTS = "menu.comments";
		public static final String MENU_EDIT = "menu.edit";
		public static final String MENU_EDIT_ENTITIES = "menu.edit.entities";
		public static final String MENU_EDIT_ENTITIES_SORT = "menu.edit.entities.sort";
		public static final String MENU_EDIT_MENTIONS = "menu.edit.mentions";
		public static final String MENU_ENTITIES = "menu.entities";
		public static final String MENU_ENTITIES_CANDIDATES = "menu.entities.candidates";
		public static final String MENU_FILE = "menu.file";
		public static final String MENU_FILE_EXPORT_AS = "menu.file.export_as";
		public static final String MENU_FILE_IMPORT_FROM = "menu.file.import_from";
		public static final String MENU_HELP = "menu.help";
		public static final String MENU_SETTINGS = "menu.settings";
		public static final String MENU_TOOLS = "menu.tools";
		public static final String MENU_TOOLS_PROC = "menu.tools.proc";
		public static final String MENU_VIEW = "menu.view";
		public static final String MENU_VIEW_FONTFAMILY = "menu.view.fontfamily";
		public static final String MENU_VIEW_STYLE = "menu.view.style";
		public static final String MESSAGE_CREATES_ENTITY = "message.creates_entity";
		public static final String MESSAGE_CREATES_MENTION = "message.creates_mention";
		public static final String MESSAGE_CREATES_MENTION_PART = "message.creates_mention_part";
		public static final String MESSAGE_ENTITY_CREATED = "message.entity_created";
		public static final String MESSAGE_LOADING = "message.loading";
		public static final String MESSAGE_MENTION_CREATED = "message.mention_created";
		public static final String MESSAGE_MENTION_PART_CREATED = "message.mention_part_created";
		public static final String MESSAGE_SAVING = "message.saving";
		public static final String SEARCH_WINDOW_TEXT_TOOLTIP = "search.window.text.tooltip";
		public static final String SEARCH_WINDOW_TITLE = "search.window.title";
		public static final String STAT_AGR_TITLE = "stat.agr.title";
		public static final String STAT_KEY_AGR_PERC = "stat.key.agr_perc";
		public static final String STAT_KEY_AGREED = "stat.key.agreed";
		public static final String STAT_KEY_AGREED_OVERALL = "stat.key.agreed.overall";
		public static final String STAT_KEY_AGREED_OVERALL_TOOLTIP = "stat.key.agreed.overall.tooltip";
		public static final String STAT_KEY_AGREED_PARALLEL = "stat.key.agreed.parallel";
		public static final String STAT_KEY_AGREED_PARALLEL_TOOLTIP = "stat.key.agreed.parallel.tooltip";
		public static final String STAT_KEY_AGREED_SELECTED = "stat.key.agreed.selected";
		public static final String STAT_KEY_AGREED_SELECTED_TOOLTIP = "stat.key.agreed.selected.tooltip";
		public static final String STAT_KEY_AGREED_TOOLTIP = "stat.key.agreed.tooltip";
		public static final String STAT_KEY_COLOR = "stat.key.color";
		public static final String STAT_KEY_COLOR_TOOLTIP = "stat.key.color.tooltip";
		public static final String STAT_KEY_ENTITIES = "stat.key.entities";
		public static final String STAT_KEY_ENTITIES_TOOLTIP = "stat.key.entities.tooltip";
		public static final String STAT_KEY_MENTIONS = "stat.key.mentions";
		public static final String STAT_KEY_MENTIONS_TOOLTIP = "stat.key.mentions.tooltip";
		public static final String STAT_KEY_POSITION = "stat.key.position";
		public static final String STAT_KEY_POSITION_TOOLTIP = "stat.key.position.tooltip";
		public static final String STAT_KEY_TOTAL = "stat.key.total";
		public static final String STAT_KEY_TOTAL_TOOLTIP = "stat.key.total.tooltip";
		public static final String STATUS_NOW_AVAILABLE = "status.now.available";
		public static final String STATUS_SEARCH_RESULTS = "status.search.results";
		public static final String STATUS_SEARCH_RESULTS_MORE_THAN = "status.search.more_than";
		public static final String STATUS_SEARCH_SELECTED_ENTITY = "status.search.selected_entity";
		public static final String STATUS_STYLE = "status.style";
		public static final String WINDOWTITLE_EDITED = "windowtitle.edited";
		public static final String WINDOWTITLE_NEW_FILE = "windowtitle.new_file";

	}

	public static final String CFG_ANNOTATOR_ID = "CFG_ANNOTATOR_ID";
	public static final String CFG_ASK_BEFORE_FILE_OVERWRITE = "CFG_ASK_FILE_OVERWRITE";
	public static final String CFG_CURRENT_DIRECTORY = "CFG_CURRENT_DIRECTORY";

	public static final String CFG_DELETE_EMPTY_ENTITIES = "CFG_DELETE_EMPTY_ENTITIES";

	public static final String CFG_FULL_TOKENS = "full_tokens";
	public static final String CFG_KEEP_TREE_SORTED = "CFG_KEEP_TREE_SORTED";
	public static final String CFG_SEARCH_RESULTS_CONTEXT = "CFG_SEARCH_RESULTS_CONTEXT";
	public static final String CFG_SHOW_TEXT_LABELS = "Show text labels";
	public static final String CFG_TRIM_WHITESPACE = "Trim Whitespace";

	public static final String CFG_WINDOWTITLE = "Windowtitle";
	public static final String ENTITY_FLAG_GENERIC = "Generic";
	public static final String ENTITY_FLAG_HIDDEN = "Hidden";

	public static final String MENTION_FLAG_AMBIGUOUS = "Ambiguous";
	public static final String MENTION_FLAG_DIFFICULT = "Difficult";

	public static final String MENTION_FLAG_NON_NOMINAL = "Non Nominal";

	public static final String PREF_RECENT = "recent_files";

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

	public static final Setting<Boolean> SETTING_KEEP_TREE_SORTED = new Setting<Boolean>(CFG_KEEP_TREE_SORTED,
			Strings.ACTION_TOGGLE_KEEP_TREE_SORTED, Strings.ACTION_TOGGLE_TRIM_WHITESPACE_TOOLTIP, true,
			MaterialDesign.MDI_SORT_VARIANT);

	public static final Setting<Boolean> SETTING_SHOW_TEXT_LABELS = new Setting<Boolean>(CFG_SHOW_TEXT_LABELS,
			Strings.ACTION_TOGGLE_SHOW_TEXT_LABELS, Strings.ACTION_TOGGLE_SHOW_TEXT_LABELS_TOOLTIP, false,
			MaterialDesign.MDI_FORMAT_TEXT);

	public static final Setting<Boolean> SETTING_TRIM_WHITESPACE = new Setting<Boolean>(CFG_TRIM_WHITESPACE,
			Strings.ACTION_TOGGLE_TRIM_WHITESPACE, Strings.ACTION_TOGGLE_TRIM_WHITESPACE_TOOLTIP, true,
			MaterialDesign.MDI_ARROW_COMPRESS);

	public static final String[] SUPPORTED_LANGUAGES = new String[] { "x-unspecified", "de", "en", "es", "fr", "it",
			"nl", "ru" };
	public static final String URL_LATEST_RELEASE_API = "https://api.github.com/repos/nilsreiter/CorefAnnotator/releases/latest";

	public static final String X_UNSPECIFIED = "x-unspecified";
}
