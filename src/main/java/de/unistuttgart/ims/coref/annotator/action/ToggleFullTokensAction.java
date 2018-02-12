package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Defaults;

public class ToggleFullTokensAction extends AnnotatorAction {
	private static final long serialVersionUID = 1L;

	public ToggleFullTokensAction(Annotator annotator) {
		super(annotator, MaterialDesign.MDI_VIEW_WEEK);
		putValue(Action.NAME, Annotator.getString("action.toggle.full_tokens"));
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.toggle.full_tokens.tooltip"));
		putValue(Action.SELECTED_KEY,
				mainApplication.getPreferences().getBoolean(Constants.CFG_FULL_TOKENS, Defaults.CFG_FULL_TOKENS));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean old = mainApplication.getPreferences().getBoolean(Constants.CFG_FULL_TOKENS, Defaults.CFG_FULL_TOKENS);
		mainApplication.getPreferences().putBoolean(Constants.CFG_FULL_TOKENS, !old);
		putValue(Action.SELECTED_KEY, !old);
	}

}
