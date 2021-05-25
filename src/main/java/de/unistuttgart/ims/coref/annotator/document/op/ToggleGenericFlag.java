package de.unistuttgart.ims.coref.annotator.document.op;

import org.apache.uima.cas.FeatureStructure;

import de.unistuttgart.ims.coref.annotator.api.v2.Flag;

public class ToggleGenericFlag extends ToggleFlag<FeatureStructure> implements CoreferenceModelOperation {

	public ToggleGenericFlag(Flag flag, Iterable<FeatureStructure> objects) {
		super(flag, objects);
	}
}