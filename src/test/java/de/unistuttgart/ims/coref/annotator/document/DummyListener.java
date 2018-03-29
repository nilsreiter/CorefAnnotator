package de.unistuttgart.ims.coref.annotator.document;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.CoreferenceModelListener;

public class DummyListener implements CoreferenceModelListener {

	public MutableList<FeatureStructureEvent> events = Lists.mutable.empty();

	@Override
	public void entityEvent(FeatureStructureEvent event) {
		events.add(event);
	}

	public void reset() {
		events.clear();
	}

}
