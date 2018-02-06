package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

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
		putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		this.mainApplication = mainApplication;
		this.documentWindow = dw;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new SearchPanel(documentWindow, mainApplication.getConfiguration()).setVisible(true);
	}
}
