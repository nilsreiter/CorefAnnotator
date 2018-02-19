package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.material.Material;

import de.unistuttgart.ims.coref.annotator.HelpWindow;

public class HelpAction extends IkonAction {

	private static final long serialVersionUID = 1L;

	public HelpAction() {
		super(Material.HELP, "action.help");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		HelpWindow.show("index");
	}

}
