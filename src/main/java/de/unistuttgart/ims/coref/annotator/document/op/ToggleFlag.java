package de.unistuttgart.ims.coref.annotator.document.op;

import org.apache.uima.cas.FeatureStructure;

public abstract class ToggleFlag<T extends FeatureStructure> extends UpdateOp<T> {

	String flag;

	@SafeVarargs
	public ToggleFlag(String flag, T... objects) {
		super(objects);
		this.flag = flag;
	}

	public ToggleFlag(String flag, Iterable<T> objects) {
		super(objects);
		this.flag = flag;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}