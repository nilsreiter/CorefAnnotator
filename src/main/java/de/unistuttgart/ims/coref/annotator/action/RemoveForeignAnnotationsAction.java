package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.SwingWorker;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public class RemoveForeignAnnotationsAction extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	public RemoveForeignAnnotationsAction(DocumentWindow dw) {
		super(dw, Constants.Strings.ACTION_REMOVE_FOREIGN_ANNOTATIONS, MaterialDesign.MDI_PHARMACY);
		putValue(Action.SHORT_DESCRIPTION, Constants.Strings.ACTION_REMOVE_FOREIGN_ANNOTATIONS_TOOLTIP);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		documentWindow.setIndeterminateProgress();
		new SwingWorker<Object, Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				documentWindow.getDocumentModel().removeForeignAnnotations();
				return null;
			}

			@Override
			protected void done() {
				documentWindow.stopIndeterminateProgress();
			}
		}.execute();
	}

}
