package de.unistuttgart.ims.coref.annotator.action;

import javax.swing.Action;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.Defaults;

public class ToggleKeepTreeSortedAction extends TogglePreferenceAction {
	private static final long serialVersionUID = 1L;

	public ToggleKeepTreeSortedAction(Annotator annotator) {
		super(annotator, MaterialDesign.MDI_SORT_VARIANT, Strings.ACTION_TOGGLE_KEEP_TREE_SORTED,
				Constants.CFG_KEEP_TREE_SORTED, Defaults.CFG_KEEP_TREE_SORTED);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_TOGGLE_KEEP_TREE_SORTED_TOOLTIP));
	}

}
