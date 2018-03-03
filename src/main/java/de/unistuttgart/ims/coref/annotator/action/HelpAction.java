package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.HelpWindow;

public class HelpAction extends AnnotatorAction {

	private static final long serialVersionUID = 1L;

	public HelpAction() {
		super(null, "action.help", MaterialDesign.MDI_HELP);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		HelpWindow.show("index");
	}

}
