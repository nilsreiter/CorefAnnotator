package de.unistuttgart.ims.coref.annotator;

import java.util.Comparator;

public enum EntitySortOrder {
	Mentions, Alphabet;

	boolean descending = true;

	public boolean isDescending() {
		return descending;
	}

	public Comparator<CATreeNode> getComparator() {

		switch (this) {
		case Mentions:
			return new Comparator<CATreeNode>() {
				@Override
				public int compare(CATreeNode o1, CATreeNode o2) {
					int l1 = o1.getChildCount();
					int l2 = o2.getChildCount();
					return (isDescending() ? -1 : 1) * Integer.compare(l1, l2);
				}
			};
		default:
			return new Comparator<CATreeNode>() {
				@Override
				public int compare(CATreeNode o1, CATreeNode o2) {
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

	public static Comparator<CATreeNode> getVisibilitySortOrder(Comparator<CATreeNode> def) {
		return new Comparator<CATreeNode>() {
			@Override
			public int compare(CATreeNode o1, CATreeNode o2) {
				if (o1.isVisible() && !o2.isVisible())
					return -1;
				if (o2.isVisible() && !o1.isVisible())
					return 1;
				return def.compare(o1, o2);
			}
		};

	}
}
