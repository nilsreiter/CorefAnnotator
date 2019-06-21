package de.unistuttgart.ims.coref.annotator;

import java.util.Comparator;

import javax.swing.tree.TreeNode;

public enum EntitySortOrder {
	Mentions, Alphabet, None;

	public boolean descending = true;

	public boolean isDescending() {
		return descending;
	}

	public Comparator<TreeNode> getComparator() {

		switch (this) {
		case None:
			return new Comparator<TreeNode>() {
				@Override
				public int compare(TreeNode o1, TreeNode o2) {
					return 0;
				}
			};
		case Mentions:
			return new Comparator<TreeNode>() {
				@Override
				public int compare(TreeNode o1, TreeNode o2) {
					int l1 = o1.getChildCount();
					int l2 = o2.getChildCount();
					return (isDescending() ? -1 : 1) * Integer.compare(l1, l2);
				}
			};
		default:
			return new Comparator<TreeNode>() {
				@Override
				public int compare(TreeNode n1, TreeNode n2) {
					CATreeNode o1 = (CATreeNode) n1;
					CATreeNode o2 = (CATreeNode) n2;

					if (!o1.isEntity() || !o2.isEntity())
						return 0;
					String l1 = o1.getEntity().getLabel();
					String l2 = o2.getEntity().getLabel();
					l1 = (l1 == null ? "null" : l1);
					l2 = (l2 == null ? "null" : l2);
					if (l1 == null || l2 == null)
						return 0;
					return (isDescending() ? -1 : 1) * l1.compareTo(l2);
				}
			};
		}

	}

	public static Comparator<TreeNode> getVisibilitySortOrder(Comparator<TreeNode> def) {
		return new Comparator<TreeNode>() {
			@Override
			public int compare(TreeNode n1, TreeNode n2) {
				CATreeNode o1 = (CATreeNode) n1;
				CATreeNode o2 = (CATreeNode) n2;

				int r = Integer.compare(o2.getRank(), o1.getRank());
				if (r == 0)
					return def.compare(o1, o2);
				return r;
			}
		};

	}
}
