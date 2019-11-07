package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.HelpWindow;
import de.unistuttgart.ims.coref.annotator.HelpWindow.HelpTopic;

public class HelpAction extends IkonAction {

	private static final long serialVersionUID = 1L;

	HelpTopic helpTopic = HelpWindow.Topic.INDEX;

	public HelpAction() {
		super("action.help", MaterialDesign.MDI_HELP);
	}

	public HelpAction(HelpTopic topic) {
		super("action.help", MaterialDesign.MDI_HELP);
		this.helpTopic = topic;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		HelpWindow.show(helpTopic);
	}

}
