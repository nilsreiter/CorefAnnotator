package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;

public class ShowLogWindowAction extends AnnotatorAction {

	private static final long serialVersionUID = 1L;

	public ShowLogWindowAction(Annotator mApp) {
		super(mApp, Constants.Strings.ACTION_SHOW_LOG, MaterialDesign.MDI_CODE_NOT_EQUAL);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainApplication.getLogWindow().setVisible(true);
	}

}
