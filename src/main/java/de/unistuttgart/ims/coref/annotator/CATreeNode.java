package de.unistuttgart.ims.coref.annotator;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.v1.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.comp.Tooltipable;

public class CATreeNode implements MutableTreeNode, Iterable<CATreeNode>, Tooltipable {

	FeatureStructure userObject;

	String label;

	int rank = 50;

	Vector<CATreeNode> children = new Vector<CATreeNode>();

	CATreeNode parent = null;

	public CATreeNode(Entity e) {
		this(e, e.getLabel());
	}

	public CATreeNode(FeatureStructure featureStructure, String label) {
		if (featureStructure != null) {
			this.userObject = featureStructure;
		}
		this.label = label;
	}

	@Override
	public Enumeration<? extends TreeNode> children() {
		return children.elements();
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public CATreeNode getChildAt(int i) {
		return children.get(i);
	}

	@Override
	public int getChildCount() {
		return children.size();
	}

	public Vector<CATreeNode> getChildren() {
		return this.children;
	}

	public Entity getEntity() {
		return (Entity) getFeatureStructure();
	}

	@SuppressWarnings("unchecked")
	public <T extends FeatureStructure> T getFeatureStructure() {
		return (T) userObject;
	}

	@Override
	public int getIndex(TreeNode node) {
		return children.indexOf(node);
	}

	public String getLabel() {
		if (this.getFeatureStructure() instanceof Annotation) {
			Annotation a = (Annotation) this.getFeatureStructure();
			return a.getCoveredText();
		}
		return label;
	}

	@Override
	public CATreeNode getParent() {
		return parent;
	}

	public int getRank() {
		return rank;
	}

	@Override
	public String getToolTip() {
		if (getUserObject() instanceof EntityGroup) {
			StringBuilder b = new StringBuilder();
			EntityGroup entityGroup = (EntityGroup) getUserObject();
			if (entityGroup.getMembers().size() > 0) {
				if (entityGroup.getMembers(0) != null && entityGroup.getMembers(0).getLabel() != null)
					b.append(entityGroup.getMembers(0).getLabel());
				else {
					System.out.println();
				}
				for (int i = 1; i < entityGroup.getMembers().size(); i++) {
					b.append(", ");
					Entity member = entityGroup.getMembers(i);
					if (member != null)
						b.append(member.getLabel());
				}
				return b.toString();
			} else {
				return null;
			}
		} else if (getUserObject() instanceof Entity) {
			return getEntity().getLabel();
		}
		return null;
	}

	public FeatureStructure getUserObject() {
		return userObject;
	}

	@Override
	public void insert(MutableTreeNode child, int index) {
		if (!getAllowsChildren()) {
			throw new IllegalStateException("node does not allow children");
		} else if (child == null) {
			throw new IllegalArgumentException("new child is null");
		} else if (isNodeAncestor(child)) {
			throw new IllegalArgumentException("new child is an ancestor");
		} else if (!(child instanceof CATreeNode))
			throw new UnsupportedOperationException();

		MutableTreeNode oldParent = (MutableTreeNode) child.getParent();

		if (oldParent != null) {
			oldParent.remove(child);
		}
		child.setParent(this);
		if (children == null) {
			children = new Vector<CATreeNode>();
		}
		children.insertElementAt((CATreeNode) child, index);
	}

	public boolean isEntity() {
		return userObject instanceof Entity;
	}

	@Override
	public boolean isLeaf() {
		return children.isEmpty();
	}

	public boolean isMention() {
		return userObject instanceof Mention;
	}

	public boolean isMentionPart() {
		return userObject instanceof DetachedMentionPart;
	}

	/**
	 * Returns true if <code>anotherNode</code> is an ancestor of this node -- if it
	 * is this node, this node's parent, or an ancestor of this node's parent. (Note
	 * that a node is considered an ancestor of itself.) If <code>anotherNode</code>
	 * is null, this method returns false. This operation is at worst O(h) where h
	 * is the distance from the root to this node.
	 *
	 * @see #isNodeDescendant
	 * @see #getSharedAncestor
	 * @param anotherNode node to test as an ancestor of this node
	 * @return true if this node is a descendant of <code>anotherNode</code>
	 */
	public boolean isNodeAncestor(TreeNode anotherNode) {
		if (anotherNode == null) {
			return false;
		}

		TreeNode ancestor = this;

		do {
			if (ancestor == anotherNode) {
				return true;
			}
		} while ((ancestor = ancestor.getParent()) != null);

		return false;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public boolean isVirtual() {
		return false;
	}

	@Override
	public Iterator<CATreeNode> iterator() {
		return new Iterator<CATreeNode>() {
			int pos = 0;

			@Override
			public boolean hasNext() {
				return pos < getChildCount();
			}

			@Override
			public CATreeNode next() {
				return getChildAt(pos++);
			}

		};
	}

	@Override
	public void remove(int index) {
		children.remove(index);

	}

	@Override
	public void remove(MutableTreeNode node) {
		children.remove(node);
	}

	/**
	 * Removes all of this node's children, setting their parents to null. If this
	 * node has no children, this method does nothing.
	 */
	public void removeAllChildren() {
		for (int i = getChildCount() - 1; i >= 0; i--) {
			remove(i);
		}
	}

	@Override
	public void removeFromParent() {
		if (parent != null) {
			parent.remove(this);
		}
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public void setParent(MutableTreeNode newParent) {
		if (!(newParent instanceof CATreeNode))
			throw new UnsupportedOperationException();
		parent = (CATreeNode) newParent;

	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public void setUserObject(FeatureStructure userObject) {
		this.userObject = userObject;
	}

	@Override
	public void setUserObject(Object object) {
		if (!(object instanceof FeatureStructure))
			throw new UnsupportedOperationException();
		userObject = (FeatureStructure) object;
	}

	@Override
	public String toString() {
		return this.getLabel();
	}

}
