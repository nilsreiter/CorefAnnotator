package de.unistuttgart.ims.coref.annotator;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

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
}
