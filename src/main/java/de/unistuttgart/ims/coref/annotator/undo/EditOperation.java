package de.unistuttgart.ims.coref.annotator.undo;

import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;

public interface EditOperation {
	void revert(CoreferenceModel model);

	void run(CoreferenceModel model);

	boolean isRun();
}
