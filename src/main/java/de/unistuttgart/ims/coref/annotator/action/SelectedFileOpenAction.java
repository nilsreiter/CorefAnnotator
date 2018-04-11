package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;

public class SelectedFileOpenAction extends IkonAction {
	private static final long serialVersionUID = 1L;
	File file;

	public SelectedFileOpenAction(Annotator mApplication, File file) {
		super(MaterialDesign.MDI_OPEN_IN_APP);
		putValue(Action.NAME, file.getName());
		putValue(Action.SHORT_DESCRIPTION, file.getPath());
		this.file = file;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Annotator.app.open(file, Annotator.app.getPluginManager().getDefaultIOPlugin(), null);
	}

}
