package de.unistuttgart.ims.coref.annotator.comp;

import java.beans.PropertyChangeListener;

public interface CABean {
	void addPropertyChangeListener(PropertyChangeListener listener);

	void removePropertyChangeListener(PropertyChangeListener listener);
}
