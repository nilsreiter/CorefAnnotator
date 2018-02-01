package de.unistuttgart.ims.coref.annotator;

import java.util.Comparator;

import de.unistuttgart.ims.coref.annotator.api.Entity;

public enum EntitySortOrder {
	Mentions, Alphabet;

	boolean descending = false;

	public boolean isDescending() {
		return descending;
	}

	public Comparator<TreeNode<Entity>> getComparator() {
		switch (this) {
		case Mentions:
			return new Comparator<TreeNode<Entity>>() {
				@Override
				public int compare(TreeNode<Entity> o1, TreeNode<Entity> o2) {
					int l1 = o1.getChildCount();
					int l2 = o2.getChildCount();
					return (isDescending() ? -1 : 1) * Integer.compare(l1, l2);
				}
			};
		default:
			return new Comparator<TreeNode<Entity>>() {
				@Override
				public int compare(TreeNode<Entity> o1, TreeNode<Entity> o2) {
					String l1 = o1.getFeatureStructure().getLabel();
					String l2 = o2.getFeatureStructure().getLabel();
					return (isDescending() ? -1 : 1) * l1.compareTo(l2);
				}
			};
		}

	}
}
