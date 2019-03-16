package de.unistuttgart.ims.coref.annotator.action;

import javax.swing.Action;

import de.unistuttgart.ims.coref.annotator.CAAbstractTreeSelectionListener;

@Deprecated
public interface CAAction extends Action {
	void setEnabled(CAAbstractTreeSelectionListener l);
}
