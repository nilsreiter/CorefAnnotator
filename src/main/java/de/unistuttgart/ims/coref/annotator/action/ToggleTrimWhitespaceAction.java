package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.material.Material;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;

public class ToggleTrimWhitespaceAction extends AnnotatorAction {
	private static final long serialVersionUID = 1L;

	public ToggleTrimWhitespaceAction(Annotator annotator) {
		super(annotator, Material.COMPARE_ARROWS);
		putValue(Action.NAME, Annotator.getString("action.toggle.trim_whitespace"));
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.toggle.trim_whitespace.tooltip"));
		putValue(Action.SELECTED_KEY,
				mainApplication.getConfiguration().getBoolean(Constants.CFG_TRIM_WHITESPACE, true));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean old = mainApplication.getConfiguration().getBoolean(Constants.CFG_TRIM_WHITESPACE, true);
		mainApplication.getConfiguration().setProperty(Constants.CFG_TRIM_WHITESPACE, !old);
		putValue(Action.SELECTED_KEY, !old);
	}

}
