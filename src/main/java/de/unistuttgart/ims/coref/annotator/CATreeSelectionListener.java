package de.unistuttgart.ims.coref.annotator;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public abstract class CATreeSelectionListener implements TreeSelectionListener {
	TreeSelectionEvent currentEvent = null;
	int num;
	JTree tree;

	// selected things
	ImmutableList<TreePath> paths;
	MutableList<CATreeNode> nodes;
	MutableList<FeatureStructure> featureStructures;

	public CATreeSelectionListener(JTree tree) {
		this.tree = tree;
	}

	protected synchronized void collectData(TreeSelectionEvent e) {
		currentEvent = e;
		num = tree.getSelectionCount();
		paths = Lists.immutable.of(tree.getSelectionPaths());
		nodes = Lists.mutable.empty();
		featureStructures = Lists.mutable.empty();

		if (num > 0)
			try {
				for (int i = 0; i < paths.size(); i++) {
					nodes.add(i, (CATreeNode) paths.get(i).getLastPathComponent());
					featureStructures.add(i, nodes.get(i).getFeatureStructure());
				}
			} catch (NullPointerException ex) {
			}

	}

	public int size() {
		return num;
	}

	public boolean isSingle() {
		return num == 1;
	}

	public boolean isDouble() {
		return num == 2;
	}

	public boolean isEntity() {
		return featureStructures.allSatisfy(f -> f instanceof Entity);
	}

	public boolean isDetachedMentionPart() {
		return featureStructures.allSatisfy(f -> f instanceof DetachedMentionPart);
	}

	public boolean isMention() {
		return featureStructures.allSatisfy(f -> f instanceof Mention);
	}

	public boolean isEntityGroup() {
		return featureStructures.allSatisfy(f -> f instanceof EntityGroup);
	}

	public boolean isLeaf() {
		for (TreeNode n : nodes)
			if (!n.isLeaf())
				return false;
		return true;
	}

	protected Entity getEntity(int i) {
		return (Entity) featureStructures.get(i);
	}

	protected Annotation getAnnotation(int i) {
		return (Annotation) featureStructures.get(i);
	}

	protected Mention getMention(int i) {
		return (Mention) featureStructures.get(i);
	}

	public ImmutableList<FeatureStructure> getFeatureStructures() {
		return featureStructures.toImmutable();
	}

}
