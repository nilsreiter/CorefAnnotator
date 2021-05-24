package de.unistuttgart.ims.coref.annotator;

import java.util.Comparator;

import de.unistuttgart.ims.coref.annotator.api.v2.Mention;

public enum EntitySortOrder {
	Mentions, Alphabet, LastModified, Position, None;

	public boolean descending = true;

	public boolean isDescending() {
		return descending;
	}

	public Comparator<CATreeNode> getComparator() {

		switch (this) {
		case None:
			return new Comparator<CATreeNode>() {
				@Override
				public int compare(CATreeNode o1, CATreeNode o2) {
					return 0;
				}
			};
		case Position:
			return new Comparator<CATreeNode>() {
				@Override
				public int compare(CATreeNode o1, CATreeNode o2) {
					Mention m1 = o1.getChildAt(0).getFeatureStructure();
					Mention m2 = o2.getChildAt(0).getFeatureStructure();
					int l1 = m1.getSurface(0).getBegin();
					int l2 = m2.getSurface(0).getBegin();
					return (isDescending() ? -1 : 1) * Integer.compare(l1, l2);
				}
			};
		case Mentions:
			return new Comparator<CATreeNode>() {
				@Override
				public int compare(CATreeNode o1, CATreeNode o2) {
					int l1 = o1.getChildCount();
					int l2 = o2.getChildCount();
					return (isDescending() ? -1 : 1) * Integer.compare(l1, l2);
				}
			};
		case LastModified:
			return new Comparator<CATreeNode>() {
				@Override
				public int compare(CATreeNode o1, CATreeNode o2) {
					long l1 = o1.getLastModified();
					long l2 = o2.getLastModified();
					if (l1 == l2) {
						l1 = o1.getChildCount();
						l2 = o2.getChildCount();
					}
					return (isDescending() ? -1 : 1) * Long.compare(l1, l2);
				}
			};
		default:
			return new Comparator<CATreeNode>() {
				@Override
				public int compare(CATreeNode o1, CATreeNode o2) {
					if (!o1.isEntity() || !o2.isEntity())
						return 0;
					String l1 = o1.getEntity().getLabel().toLowerCase();
					String l2 = o2.getEntity().getLabel().toLowerCase();
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
				int r = Integer.compare(o2.getRank(), o1.getRank());
				if (r == 0)
					return def.compare(o1, o2);
				return r;
			}
		};

	}
}
