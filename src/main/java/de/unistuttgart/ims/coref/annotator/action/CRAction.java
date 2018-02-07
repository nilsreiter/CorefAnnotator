package de.unistuttgart.ims.coref.annotator.action;

import javax.swing.AbstractAction;

import de.unistuttgart.ims.coref.annotator.CoreferenceModel;

@Deprecated
public abstract class CRAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	CoreferenceModel model;

	public CRAction(CoreferenceModel cm) {
		model = cm;
	}

}
