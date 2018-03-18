package de.unistuttgart.ims.coref.annotator.document;

public abstract class AbstractEditOperation implements EditOperationDescription {
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
