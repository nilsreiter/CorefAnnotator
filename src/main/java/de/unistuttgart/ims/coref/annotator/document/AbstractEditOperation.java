package de.unistuttgart.ims.coref.annotator.document;

import de.unistuttgart.ims.coref.annotator.document.op.Operation;

public abstract class AbstractEditOperation implements Operation {
	boolean run = false;

	public void run(CoreferenceModel model) {
		run = true;
		perform(model);
	}

	abstract void perform(CoreferenceModel model);

	public boolean isRun() {
		return run;
	};

}
