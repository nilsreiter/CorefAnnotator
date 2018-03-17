package de.unistuttgart.ims.coref.annotator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class CATreeNode extends DefaultMutableTreeNode implements Iterable<CATreeNode> {

	private static Map<Integer, FeatureStructure> mentionCache = new HashMap<Integer, FeatureStructure>();

	private static final long serialVersionUID = 1L;

	transient FeatureStructure featureStructure = null;

	int featureStructureHash;

	String label;

	int rank = 50;

	public CATreeNode(FeatureStructure featureStructure, String label) {
		if (featureStructure != null) {
			this.featureStructure = featureStructure;
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
		if (featureStructure == null)
			featureStructure = mentionCache.get(featureStructureHash);
		return (T) featureStructure;
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
		return featureStructure instanceof Entity;
	}

	public boolean isMention() {
		return featureStructure instanceof Mention;
	}

	public boolean isMentionPart() {
		return featureStructure instanceof DetachedMentionPart;
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

}
