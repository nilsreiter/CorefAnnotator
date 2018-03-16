package de.unistuttgart.ims.coref.annotator.undo;

import de.unistuttgart.ims.coref.annotator.CoreferenceModel;

public interface EditOperation {
	public void revert(CoreferenceModel model);
}
