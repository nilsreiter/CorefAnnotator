package de.unistuttgart.ims.coref.annotator.document.op;

import org.apache.uima.cas.FeatureStructure;
import org.eclipse.collections.api.list.ImmutableList;

import de.unistuttgart.ims.coref.annotator.api.v1.Flag;

public class DeleteFlag implements FlagModelOperation {
	Flag flag;
	ImmutableList<FeatureStructure> featureStructures;

	public DeleteFlag(Flag flag) {
		this.flag = flag;
	}

	public Flag getFlag() {
		return flag;
	}

	public void setFlag(Flag flag) {
		this.flag = flag;
	}

	public ImmutableList<FeatureStructure> getFeatureStructures() {
		return featureStructures;
	}

	public void setFeatureStructures(ImmutableList<FeatureStructure> mentions) {
		this.featureStructures = mentions;
	}
}
