package de.unistuttgart.ims.coref.annotator.document.op;

import de.unistuttgart.ims.coref.annotator.api.v1.Flag;

public class DeleteFlag implements FlagModelOperation {
	Flag flag;

	public DeleteFlag(Flag flag) {
		this.flag = flag;
	}

	public Flag getFlag() {
		return flag;
	}

	public void setFlag(Flag flag) {
		this.flag = flag;
	}
}
