package de.unistuttgart.ims.coref.annotator;

import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;

public interface CoreferenceModelListener {

	void entityEvent(FeatureStructureEvent event);

}
