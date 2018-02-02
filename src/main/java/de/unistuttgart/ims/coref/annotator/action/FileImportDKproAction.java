package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CoreferenceFlavor;

public class FileImportDKproAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	Annotator mainApplication;

	public FileImportDKproAction(Annotator mApplication) {
		putValue(Action.NAME, "DKpro");
		mainApplication = mApplication;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainApplication.fileOpenDialog(CoreferenceFlavor.DKpro);

	}
}
