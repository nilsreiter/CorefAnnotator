package de.unistuttgart.ims.coref.annotator;

import de.unistuttgart.ims.coref.annotator.api.Mention;

public interface CoreferenceModelListener {
	public void mentionAdded(Mention m);

	public void mentionChanged(Mention m);

	public void mentionSelected(Mention m);

	void mentionRemoved(Mention m);

}
