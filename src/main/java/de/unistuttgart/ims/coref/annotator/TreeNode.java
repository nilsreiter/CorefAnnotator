package de.unistuttgart.ims.coref.annotator;

import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class TreeNode<T extends FeatureStructure> extends DefaultMutableTreeNode {

	static Map<Integer, FeatureStructure> mentionCache = new HashMap<Integer, FeatureStructure>();

	private static final long serialVersionUID = 1L;

	int featureStructureHash;

	String label;

	public TreeNode(T featureStructure, String label) {
		if (featureStructure != null) {
			this.featureStructureHash = featureStructure.hashCode();
			mentionCache.put(featureStructure.hashCode(), featureStructure);
		}
		this.label = label;
	}

	@Deprecated
	public void registerDrop(DefaultTreeModel treeModel, DocumentWindow dw, TreeNode<Mention> tn) {
		Mention m = tn.getFeatureStructure();

		if (getFeatureStructure() instanceof Entity) {
			m.setEntity((Entity) getFeatureStructure());
			treeModel.insertNodeInto(tn, this, 0);
		} else if (getFeatureStructure() == null) {
			Entity e = new Entity(dw.getJcas());
			e.addToIndexes();
			String s = dw.getJcas().getDocumentText().substring(m.getBegin(), m.getEnd());
			TreeNode<Entity> node = new TreeNode<Entity>(e, s);
			treeModel.insertNodeInto(node, this, 0);
			m.setEntity(e);
			treeModel.insertNodeInto(tn, node, 0);
		}
		dw.getViewer().drawAnnotation(m);
		treeModel.reload();
	}

	@Deprecated
	public void registerDrop(DefaultTreeModel treeModel, PotentialAnnotation anno) {
		if (getFeatureStructure() instanceof Entity) {
			System.err.println("adding new mention to existing entity");
			Mention m = AnnotationFactory.createAnnotation(anno.getTextView().getJCas(), anno.getBegin(), anno.getEnd(),
					Mention.class);
			m.setEntity((Entity) getFeatureStructure());
			TreeNode<Mention> mNode = new TreeNode<Mention>(m, null);
			treeModel.insertNodeInto(mNode, this, 0);

		} else if (getFeatureStructure() == null || getFeatureStructure() instanceof TOP) {
			System.err.println("new entity received");
			Entity e = new Entity(anno.getTextView().getJCas());
			e.addToIndexes();
			String s = anno.getTextView().getJCas().getDocumentText().substring(anno.getBegin(), anno.getEnd());
			TreeNode<Entity> node = new TreeNode<Entity>(e, s);
			treeModel.insertNodeInto(node, this, 0);
			System.err.println(" added to model");

			Mention m = AnnotationFactory.createAnnotation(anno.getTextView().getJCas(), anno.getBegin(), anno.getEnd(),
					Mention.class);
			m.setEntity(e);
			TreeNode<Mention> mNode = new TreeNode<Mention>(m, null);
			treeModel.insertNodeInto(mNode, node, 0);

		}
	}

	public T getFeatureStructure() {
		return (T) mentionCache.get(featureStructureHash);
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

	public void setLabel(String label) {
		this.label = label;
	}

}
