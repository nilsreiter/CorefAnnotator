package de.unistuttgart.ims.coref.annotator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class TreeNode<T extends FeatureStructure> extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;

	T featureStructure;

	String label;

	public TreeNode(T featureStructure, String label) {
		this.featureStructure = featureStructure;
		this.label = label;
	}

	public void registerDrop(DefaultTreeModel treeModel, PotentialAnnotation anno) {
		if (featureStructure instanceof Entity) {
			System.err.println("adding new mention to existing entity");
			Mention m = AnnotationFactory.createAnnotation(anno.getTextView().getJCas(), anno.getBegin(), anno.getEnd(),
					Mention.class);
			m.setEntity((Entity) featureStructure);
			TreeNode<Mention> mNode = new TreeNode<Mention>(m, null);
			treeModel.insertNodeInto(mNode, this, 0);

		} else if (featureStructure == null || featureStructure instanceof TOP) {
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
		return featureStructure;
	}

	public void setFeatureStructure(T featureStructure) {
		this.featureStructure = featureStructure;
	}

	@Override
	public String toString() {
		return this.getLabel();
	}

	public String getLabel() {
		if (this.featureStructure instanceof Annotation) {
			Annotation a = (Annotation) this.featureStructure;
			return a.getCoveredText();
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
