package de.unistuttgart.ims.coref.annotator;

import javax.swing.event.TreeSelectionListener;

public interface CATreeSelectionListener extends TreeSelectionListener {
	void valueChanged(CATreeSelectionEvent e);
}
