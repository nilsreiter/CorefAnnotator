package de.unistuttgart.ims.coref.annotator;

import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

public class Util {
	private static String[] languageNames = null;

	public static String toString(TreeModel tm) {
		return toString((TreeNode) tm.getRoot(), 0);
	}

	public static String toString(Object o) {
		if (o == null)
			return "null";
		return o.toString();
	}

	public static String toString(javax.swing.tree.TreeNode tn, int level) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < level; i++)
			b.append("-");
		b.append(tn.toString()).append("\n");
		for (int j = 0; j < tn.getChildCount(); j++) {
			b.append(toString(tn.getChildAt(j), level + 1));
		}
		return b.toString();

	}

	public static String[] getSupportedLanguageNames() {
		if (languageNames == null) {
			languageNames = new String[Constants.SUPPORTED_LANGUAGES.length];
			for (int i = 0; i < languageNames.length; i++) {
				languageNames[i] = new Locale(Constants.SUPPORTED_LANGUAGES[i]).getDisplayLanguage();
			}
			Arrays.sort(languageNames);
		}
		return languageNames;
	}

	public static String getLanguageName(String iso) {
		return new Locale(iso).getDisplayLanguage();
	}

	public static String getLanguage(String languageName) {
		getSupportedLanguageNames();
		for (int i = 0; i < Constants.SUPPORTED_LANGUAGES.length; i++)
			if (languageName == getLanguageName(Constants.SUPPORTED_LANGUAGES[i]))
				return Constants.SUPPORTED_LANGUAGES[i];

		return null;
	}

	public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
		int x = Constants.RANDOM.nextInt(clazz.getEnumConstants().length);
		return clazz.getEnumConstants()[x];
	}

	public static String format(String formatString, Object... objects) {
		StringBuilder b = new StringBuilder();
		try (Formatter formatter = new Formatter(b)) {
			formatter.format(Locale.getDefault(), formatString, objects);
			return b.toString();
		}
	}

}
