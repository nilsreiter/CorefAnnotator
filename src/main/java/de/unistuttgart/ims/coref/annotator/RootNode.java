package de.unistuttgart.ims.coref.annotator;

import java.util.Iterator;

public class RootNode extends CATreeNode implements Iterable<EntityTreeNode> {

	private static final long serialVersionUID = 1L;

	public RootNode(String label) {
		super(null, label);
	}

	@Override
	public Iterator<EntityTreeNode> iterator() {
		return new Iterator<EntityTreeNode>() {
			int pos = 0;

			@Override
			public boolean hasNext() {
				return pos < getChildCount();
			}

			@Override
			public EntityTreeNode next() {
				return (EntityTreeNode) getChildAt(pos++);
			}

		};
	}
}
