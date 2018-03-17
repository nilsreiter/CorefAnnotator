package de.unistuttgart.ims.coref.annotator.undo;

import de.unistuttgart.ims.coref.annotator.CoreferenceModel;

public abstract class AbstractEditOperation implements EditOperation {
	boolean run = false;

	@Override
	public void run(CoreferenceModel model) {
		run = true;
		perform(model);
	}

	abstract void perform(CoreferenceModel model);

	@Override
	public boolean isRun() {
		return run;
	};

}
