package de.unistuttgart.ims.coref.annotator.comp;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public interface ExtendedChangeListener extends ChangeListener {
	void beforeStateChanged(ChangeEvent e);

}
