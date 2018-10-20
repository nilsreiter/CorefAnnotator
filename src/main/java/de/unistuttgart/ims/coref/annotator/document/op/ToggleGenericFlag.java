package de.unistuttgart.ims.coref.annotator.document.op;

import org.apache.uima.cas.FeatureStructure;

public class ToggleGenericFlag extends ToggleFlag<FeatureStructure> {

	public ToggleGenericFlag(String flag, Iterable<FeatureStructure> objects) {
		super(flag, objects);
	}
}