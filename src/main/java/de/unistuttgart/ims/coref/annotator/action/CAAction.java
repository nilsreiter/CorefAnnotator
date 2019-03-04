package de.unistuttgart.ims.coref.annotator.action;

import javax.swing.Action;

import de.unistuttgart.ims.coref.annotator.CAAbstractTreeSelectionListener;

public interface CAAction extends Action {
	void setEnabled(CAAbstractTreeSelectionListener l);
}
