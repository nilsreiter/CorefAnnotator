package de.unistuttgart.ims.coref.annotator;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

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
}
