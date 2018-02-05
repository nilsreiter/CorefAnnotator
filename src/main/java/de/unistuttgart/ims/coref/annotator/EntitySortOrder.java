package de.unistuttgart.ims.coref.annotator;

import java.util.Comparator;

public enum EntitySortOrder {
	Mentions, Alphabet;

	boolean descending = false;

	public boolean isDescending() {
		return descending;
	}

	public Comparator<EntityTreeNode> getComparator() {
		switch (this) {
		case Mentions:
			return new Comparator<EntityTreeNode>() {
				@Override
				public int compare(EntityTreeNode o1, EntityTreeNode o2) {
					int l1 = o1.getChildCount();
					int l2 = o2.getChildCount();
					return (isDescending() ? -1 : 1) * Integer.compare(l1, l2);
				}
			};
		default:
			return new Comparator<EntityTreeNode>() {
				@Override
				public int compare(EntityTreeNode o1, EntityTreeNode o2) {
					if (o1.getFeatureStructure() == null && o2.getFeatureStructure() != null)
						return 1;
					if (o2.getFeatureStructure() == null && o1.getFeatureStructure() != null)
						return -1;
					String l1 = o1.getFeatureStructure().getLabel();
					String l2 = o2.getFeatureStructure().getLabel();
					if (l1 == null || l2 == null)
						return 0;
					return (isDescending() ? -1 : 1) * l1.compareTo(l2);
				}
			};
		}

	}
}
