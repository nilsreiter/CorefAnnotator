package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Defaults;

public class SetAnnotatorNameAction extends IkonAction {

	private static final long serialVersionUID = 1L;

	public SetAnnotatorNameAction(Annotator mApp) {
		super(Constants.Strings.ACTION_SET_ANNOTATOR_NAME, MaterialDesign.MDI_ACCOUNT_CARD_DETAILS);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String current = Annotator.app.getPreferences().get(Constants.CFG_ANNOTATOR_ID, Defaults.CFG_ANNOTATOR_ID);
		String s = JOptionPane
				.showInputDialog(Annotator.getString(Constants.Strings.DIALOG_CHANGE_ANNOTATOR_NAME_PROMPT), current);
		Annotator.app.getPreferences().put(Constants.CFG_ANNOTATOR_ID, s);
	}

}
