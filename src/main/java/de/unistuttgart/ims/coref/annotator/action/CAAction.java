package de.unistuttgart.ims.coref.annotator.action;

import javax.swing.Action;

import de.unistuttgart.ims.coref.annotator.CATreeSelectionListener;

public interface CAAction extends Action {
	void setEnabled(CATreeSelectionListener l);
}
