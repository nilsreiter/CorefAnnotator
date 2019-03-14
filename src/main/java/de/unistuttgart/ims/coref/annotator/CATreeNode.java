package de.unistuttgart.ims.coref.annotator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.v1.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.comp.Tooltipable;

public class CATreeNode extends DefaultMutableTreeNode implements Iterable<CATreeNode>, Tooltipable {

	private static Map<Integer, FeatureStructure> mentionCache = new HashMap<Integer, FeatureStructure>();

	private static final long serialVersionUID = 1L;

	int featureStructureHash;

	String label;

	int rank = 50;

	public CATreeNode(FeatureStructure featureStructure, String label) {
		if (featureStructure != null) {
			this.userObject = featureStructure;
			this.featureStructureHash = featureStructure.hashCode();
			mentionCache.put(featureStructure.hashCode(), featureStructure);
		}
		this.label = label;
	}

	public CATreeNode(Entity e) {
		this(e, e.getLabel());
	}

	public Entity getEntity() {
		return getFeatureStructure();
	}

	@SuppressWarnings("unchecked")
	public <T extends FeatureStructure> T getFeatureStructure() {
		if (userObject == null)
			userObject = mentionCache.get(featureStructureHash);
		return (T) userObject;
	}

	@Override
	public String toString() {
		return this.getLabel();
	}

	public String getLabel() {
		if (this.getFeatureStructure() instanceof Annotation) {
			Annotation a = (Annotation) this.getFeatureStructure();
			return a.getCoveredText();
		}
		return label;
	}

	@Override
	public CATreeNode getChildAt(int i) {
		return (CATreeNode) super.getChildAt(i);
	}

	@Override
	public CATreeNode getParent() {
		return (CATreeNode) super.getParent();
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isVirtual() {
		return false;
	}

	public boolean isEntity() {
		return userObject instanceof Entity;
	}

	public boolean isMention() {
		return userObject instanceof Mention;
	}

	public boolean isMentionPart() {
		return userObject instanceof DetachedMentionPart;
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

	@SuppressWarnings("unchecked")
	public Vector<CATreeNode> getChildren() {
		return this.children;
	}

	public void setChildren(Vector<CATreeNode> vec) {
		this.children = vec;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
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
					b.append(entityGroup.getMembers(i).getLabel());
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

}
