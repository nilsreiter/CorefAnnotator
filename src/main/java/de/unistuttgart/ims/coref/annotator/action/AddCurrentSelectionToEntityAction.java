package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import de.unistuttgart.ims.coref.annotator.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.api.Entity;

public class AddCurrentSelectionToEntityAction extends CRAction {

	private static final long serialVersionUID = 1L;
	Entity entity;

	public AddCurrentSelectionToEntityAction(CoreferenceModel cm, Entity entity, String entityLabel, int number) {
		super(cm);

	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		// int b = this.model.getTextView().getTextPane().getSelectionStart();
		// int e = this.model.getTextView().getTextPane().getSelectionEnd();
		// model.addNewMention(entity, b, e);
	}

}
