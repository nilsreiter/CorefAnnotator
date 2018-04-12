package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public class ShowSearchPanelAction extends DocumentWindowAction {
	private static final long serialVersionUID = 1L;

	Annotator mainApplication;

	public ShowSearchPanelAction(Annotator mainApplication, DocumentWindow dw) {
		super(dw, Constants.Strings.ACTION_SEARCH, MaterialDesign.MDI_FILE_FIND);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.search.tooltip"));
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		this.mainApplication = mainApplication;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().showSearch();
	}
}
