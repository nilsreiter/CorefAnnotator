package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.plugins.io.quadrama.QuaDramAPlugin;

public class FileImportQuaDramAAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	Annotator mainApplication;

	public FileImportQuaDramAAction(Annotator mApplication) {
		putValue(Action.NAME, "QuaDramA");
		mainApplication = mApplication;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainApplication.fileOpenDialog(new QuaDramAPlugin());

	}
}
