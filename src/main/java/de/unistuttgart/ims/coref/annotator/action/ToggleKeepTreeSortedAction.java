package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.Defaults;

public class ToggleKeepTreeSortedAction extends AnnotatorAction {
	private static final long serialVersionUID = 1L;

	public ToggleKeepTreeSortedAction(Annotator annotator) {
		super(annotator, MaterialDesign.MDI_SORT_VARIANT, Strings.ACTION_TOGGLE_KEEP_TREE_SORTED);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_TOGGLE_KEEP_TREE_SORTED_TOOLTIP));
		putValue(Action.SELECTED_KEY, mainApplication.getPreferences().getBoolean(Constants.CFG_KEEP_TREE_SORTED,
				Defaults.CFG_KEEP_TREE_SORTED));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean old = mainApplication.getPreferences().getBoolean(Constants.CFG_KEEP_TREE_SORTED,
				Defaults.CFG_KEEP_TREE_SORTED);
		mainApplication.getPreferences().putBoolean(Constants.CFG_KEEP_TREE_SORTED, !old);
		putValue(Action.SELECTED_KEY, !old);
	}

}
