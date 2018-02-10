package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.kordamp.ikonli.material.Material;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.SearchPanel;

public class ShowSearchPanelAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	Annotator mainApplication;
	DocumentWindow documentWindow;

	public ShowSearchPanelAction(Annotator mainApplication, DocumentWindow dw) {
		super();
		putValue(Action.NAME, Annotator.getString("action.search"));
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString("action.search.tooltip"));
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		putValue(Action.LARGE_ICON_KEY, FontIcon.of(Material.FIND_IN_PAGE));
		putValue(Action.SMALL_ICON, FontIcon.of(Material.FIND_IN_PAGE));
		this.mainApplication = mainApplication;
		this.documentWindow = dw;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new SearchPanel(documentWindow, mainApplication.getPreferences()).setVisible(true);
	}
}
