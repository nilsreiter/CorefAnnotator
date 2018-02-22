package de.unistuttgart.ims.coref.annotator.action;

import javax.swing.Action;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.Defaults;

@Deprecated
public class ToggleFullTokensAction extends TogglePreferenceAction {
	private static final long serialVersionUID = 1L;

	public ToggleFullTokensAction(Annotator annotator) {
		super(annotator, MaterialDesign.MDI_VIEW_WEEK, Strings.ACTION_TOGGLE_FULL_TOKENS, Constants.CFG_FULL_TOKENS,
				Defaults.CFG_FULL_TOKENS);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_TOGGLE_FULL_TOKENS_TOOLTIP));
	}

}
