package de.unistuttgart.ims.coref.annotator.undo;

import de.unistuttgart.ims.coref.annotator.CoreferenceModel;

public interface EditOperation {
	void revert(CoreferenceModel model);

	void run(CoreferenceModel model);

	boolean isRun();
}
