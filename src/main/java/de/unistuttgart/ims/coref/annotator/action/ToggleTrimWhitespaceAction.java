package de.unistuttgart.ims.coref.annotator.action;

import javax.swing.Action;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Defaults;

@Deprecated
public class ToggleTrimWhitespaceAction extends TogglePreferenceAction {
	private static final long serialVersionUID = 1L;

	public ToggleTrimWhitespaceAction(Annotator annotator) {
		super(annotator, MaterialDesign.MDI_ARROW_COMPRESS, Constants.Strings.ACTION_TOGGLE_TRIM_WHITESPACE,
				Constants.CFG_TRIM_WHITESPACE, Defaults.CFG_TRIM_WHITESPACE);
		putValue(Action.SHORT_DESCRIPTION,
				Annotator.getString(Constants.Strings.ACTION_TOGGLE_TRIM_WHITESPACE_TOOLTIP));
	}

}
