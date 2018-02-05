package de.unistuttgart.ims.coref.annotator;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.tcas.Annotation;

public class CATreeNode extends DefaultMutableTreeNode {

	// static Map<Integer, FeatureStructure> mentionCache = new HashMap<Integer,
	// FeatureStructure>();

	private static final long serialVersionUID = 1L;

	transient FeatureStructure featureStructure = null;

	// transient int featureStructureHash;

	String label;

	public CATreeNode(FeatureStructure featureStructure, String label) {
		if (featureStructure != null) {
			this.featureStructure = featureStructure;
			// this.featureStructureHash = featureStructure.hashCode();
			// mentionCache.put(featureStructure.hashCode(), featureStructure);
		}
		this.label = label;
	}

	public FeatureStructure getFeatureStructure() {
		return featureStructure;
		// return mentionCache.get(featureStructureHash);
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

	public boolean isVirtual() {
		return false;
	}

}
