package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;

public class ShowLogWindowAction extends IkonAction {

	private static final long serialVersionUID = 1L;

	public ShowLogWindowAction(Annotator mApp) {
		super(Constants.Strings.ACTION_SHOW_LOG, MaterialDesign.MDI_CODE_NOT_EQUAL);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Annotator.app.getLogWindow().setVisible(true);
	}

}
