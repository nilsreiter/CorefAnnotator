package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class OperationAction<O> extends AbstractAction {

	private static final long serialVersionUID = 1L;

	Class<O> operationClass;

	public OperationAction(Class<O> operationClass) {
		this.operationClass = operationClass;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

}
