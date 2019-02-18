package de.unistuttgart.ims.coref.annotator;

import java.util.Arrays;
import java.util.Collection;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.Meta;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

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

	public static boolean contains(StringArray array, String s) {
		if (array == null)
			return false;
		for (int i = 0; i < array.size(); i++)
			if (array.get(i).equals(s))
				return true;
		return false;
	}

	public static boolean contains(FSArray array, FeatureStructure s) {
		if (array == null)
			return false;
		for (int i = 0; i < array.size(); i++)
			if (array.get(i) == s)
				return true;
		return false;
	}

	public static void addFlagKey(FeatureStructure fs, String flagKey) {
		Feature feature = fs.getType().getFeatureByBaseName("Flags");
		try {
			StringArray arr = addTo(fs.getCAS().getJCas(), (StringArray) fs.getFeatureValue(feature), flagKey);
			fs.setFeatureValue(feature, arr);
		} catch (CASRuntimeException | CASException e) {
			e.printStackTrace();
		}
	}

	public static void removeFlagKey(FeatureStructure fs, String flagKey) {
		Feature feature = fs.getType().getFeatureByBaseName("Flags");
		try {
			StringArray arr = removeFrom(fs.getCAS().getJCas(), (StringArray) fs.getFeatureValue(feature), flagKey);
			fs.setFeatureValue(feature, arr);
		} catch (CASRuntimeException | CASException e) {
			e.printStackTrace();
		}

	}

	public static StringArray removeFrom(JCas jcas, StringArray arr, String fs) {
		int i = 0, j = 0;
		StringArray nArr = null;
		int oldSize = arr == null ? 0 : arr.size();
		nArr = new StringArray(jcas, oldSize - 1);
		for (; i < oldSize; i++, j++) {
			if (!arr.get(i).equals(fs))
				nArr.set(j, arr.get(i));
			else
				j--;
		}
		return nArr;
	}

	public static FSArray removeFrom(JCas jcas, FSArray arr, FeatureStructure fs) {
		int i = 0, j = 0;
		FSArray nArr = null;
		arr.removeFromIndexes();
		int oldSize = arr == null ? 0 : arr.size();
		nArr = new FSArray(jcas, oldSize - 1);
		for (; i < oldSize; i++, j++) {
			if (!arr.get(i).equals(fs))
				nArr.set(j, arr.get(i));
			else
				j--;
		}
		nArr.addToIndexes();
		return nArr;
	}

	public static StringArray addTo(JCas jcas, StringArray arr, String fs) {
		int i = 0;
		StringArray nArr;
		int oldSize = arr == null ? 0 : arr.size();
		if (arr != null) {
			nArr = new StringArray(jcas, oldSize + 1);
			for (; i < oldSize; i++) {
				nArr.set(i, arr.get(i));
			}
		} else {
			nArr = new StringArray(jcas, 1);
		}
		nArr.set(i, fs);
		if (arr != null)
			arr.removeFromIndexes();
		nArr.addToIndexes();
		return nArr;

	}

	public static FSArray addTo(JCas jcas, FSArray arr, FeatureStructure fs) {
		int i = 0;
		FSArray nArr;
		if (arr != null) {
			nArr = new FSArray(jcas, arr.size() + 1);
			for (; i < arr.size(); i++) {
				nArr.set(i, arr.get(i));
			}
		} else {
			nArr = new FSArray(jcas, 1);
		}
		nArr.set(i, fs);
		arr.removeFromIndexes();
		nArr.addToIndexes();
		return nArr;

	}

	public static boolean isX(FeatureStructure fs, String flag) {
		Feature feature = fs.getType().getFeatureByBaseName("Flags");
		return Util.contains((StringArray) fs.getFeatureValue(feature), flag);
	}

	public static boolean isGeneric(Entity e) {
		return Util.contains(e.getFlags(), Constants.ENTITY_FLAG_GENERIC);
	}

	public static boolean isDifficult(Mention m) {
		return Util.contains(m.getFlags(), Constants.MENTION_FLAG_DIFFICULT);
	}

	public static boolean isNonNominal(Mention m) {
		return Util.contains(m.getFlags(), Constants.MENTION_FLAG_NON_NOMINAL);
	}

	public static boolean isAmbiguous(Mention m) {
		return Util.contains(m.getFlags(), Constants.MENTION_FLAG_AMBIGUOUS);
	}

	public static Meta getMeta(JCas jcas) {
		if (!JCasUtil.exists(jcas, Meta.class)) {
			Meta m = new Meta(jcas);
			m.addToIndexes();
			return m;
		}
		try {
			return JCasUtil.selectSingle(jcas, Meta.class);
		} catch (IllegalArgumentException e) {
			Annotator.logger.catching(e);
			return null;
		}
	}

	public static <T extends Annotation> T extend(T annotation) {
		final char[] s = annotation.getCoveredText().toCharArray();
		char[] text = annotation.getCAS().getDocumentText().toCharArray();
		if (s.length == 0)
			return annotation;

		int b = annotation.getBegin(), e = annotation.getEnd();

		if (b > 0) {
			char prev = text[b - 1];
			while (b > 0 && Character.isLetter(prev)) {
				b--;
				// if we have reached the beginning, we pretend the
				// previous character to be a white space.
				if (b == 0)
					prev = ' ';
				else
					prev = text[b - 1];
			}
		}

		if (e < text.length) {
			char next = text[e];
			while (e < text.length && Character.isLetter(next)) {
				e++;
				if (e == text.length)
					next = ' ';
				else
					next = text[e];
			}
		}

		annotation.setBegin(b);
		annotation.setEnd(e);
		return annotation;
	}

	public static String[] getSupportedLanguageNames() {
		if (languageNames == null) {
			languageNames = new String[Constants.SUPPORTED_LANGUAGES.length];
			for (int i = 0; i < languageNames.length; i++) {
				languageNames[i] = Annotator.getString("language." + Constants.SUPPORTED_LANGUAGES[i]);
			}
			Arrays.sort(languageNames);
		}
		return languageNames;
	}

	public static String getLanguageName(String iso) {
		return Annotator.getString("language." + iso);
	}

	public static String getLanguage(String languageName) {
		getSupportedLanguageNames();
		for (int i = 0; i < Constants.SUPPORTED_LANGUAGES.length; i++)
			if (languageName == getLanguageName(Constants.SUPPORTED_LANGUAGES[i]))
				return Constants.SUPPORTED_LANGUAGES[i];

		return null;
	}

	public static <T extends TOP> int count(JCas jcas, Class<T> cl) {
		return JCasUtil.select(jcas, cl).size();
	}

	@SuppressWarnings("unchecked")
	public static <T extends FeatureStructure> MutableList<T> toList(FSArray arr) {
		MutableList<T> list = Lists.mutable.empty();
		arr.forEach(fs -> list.add((T) fs));
		return list;
	}

	public static FSArray toFSArray(JCas jcas, Collection<? extends FeatureStructure> fs) {
		FSArray arr = new FSArray(jcas, fs.size());
		int i = 0;
		for (FeatureStructure f : fs) {
			arr.set(i++, f);
		}
		arr.addToIndexes();
		return arr;
	}

	public static StringArray getFlags(FeatureStructure fs) throws CASException {
		Feature feature = fs.getType().getFeatureByBaseName("Flags");
		if (feature == null)
			return new StringArray(fs.getCAS().getJCas(), 0);
		else {
			StringArray sa = (StringArray) fs.getFeatureValue(feature);
			if (sa == null)
				return new StringArray(fs.getCAS().getJCas(), 0);
			else
				return sa;
		}
	}

	public static String[] getFlagsAsStringArray(FeatureStructure fs) {
		Feature feature = fs.getType().getFeatureByBaseName("Flags");
		if (feature == null)
			return new String[0];
		else {
			StringArray sa = (StringArray) fs.getFeatureValue(feature);
			if (sa == null)
				return new String[0];
			else
				return sa.toStringArray();
		}
	}

	public static void setFlags(FeatureStructure fs, StringArray arr) throws CASException {
		Feature feature = fs.getType().getFeatureByBaseName("Flags");
		if (feature == null)
			return;
		else
			fs.setFeatureValue(feature, arr);
	}

	public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
		int x = Constants.RANDOM.nextInt(clazz.getEnumConstants().length);
		return clazz.getEnumConstants()[x];
	}

}
