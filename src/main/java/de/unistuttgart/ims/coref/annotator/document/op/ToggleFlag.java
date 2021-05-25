package de.unistuttgart.ims.coref.annotator.document.op;

import org.apache.uima.cas.FeatureStructure;

import de.unistuttgart.ims.coref.annotator.api.v2.Flag;

public abstract class ToggleFlag<T extends FeatureStructure> extends UpdateOperation<T> {

	Flag flag;

	@SafeVarargs
	public ToggleFlag(Flag flag, T... objects) {
		super(objects);
		this.flag = flag;
	}

	public ToggleFlag(Flag flag, Iterable<T> objects) {
		super(objects);
		this.flag = flag;
	}

	public Flag getFlag() {
		return flag;
	}

	public void setFlag(Flag flag) {
		this.flag = flag;
	}

}