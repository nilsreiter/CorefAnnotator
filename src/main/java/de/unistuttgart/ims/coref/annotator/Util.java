package de.unistuttgart.ims.coref.annotator;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.api.Meta;

public class Util {
	public static String toString(TreeModel tm) {
		return toString((TreeNode) tm.getRoot(), 0);
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

	public static boolean isGeneric(Entity e) {
		return Util.contains(e.getFlags(), Constants.ENTITY_FLAG_GENERIC);
	}

	public static boolean isDifficult(Mention m) {
		return Util.contains(m.getFlags(), Constants.MENTION_FLAG_DIFFICULT);
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

		char prev = text[b - 1];
		while (b > 0 && Character.isLetter(prev)) {
			prev = text[(--b) - 1];
		}

		char next = text[e];
		while (e < text.length && Character.isLetter(next))
			next = text[(++e)];

		annotation.setBegin(b);
		annotation.setEnd(e);
		return annotation;
	}

}
