package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.material.Material;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;

public class ToggleTrimWhitespaceAction extends AnnotatorAction {
	private static final long serialVersionUID = 1L;

	public ToggleTrimWhitespaceAction(Annotator annotator) {
		super(annotator, Material.COMPARE_ARROWS, Constants.Strings.ACTION_TOGGLE_TRIM_WHITESPACE);
		putValue(Action.SHORT_DESCRIPTION,
				Annotator.getString(Constants.Strings.ACTION_TOGGLE_TRIM_WHITESPACE_TOOLTIP));
		putValue(Action.SELECTED_KEY, mainApplication.getPreferences().getBoolean(Constants.CFG_TRIM_WHITESPACE, true));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean old = mainApplication.getPreferences().getBoolean(Constants.CFG_TRIM_WHITESPACE, true);
		mainApplication.getPreferences().putBoolean(Constants.CFG_TRIM_WHITESPACE, !old);
		putValue(Action.SELECTED_KEY, !old);
	}

}
