package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.SwingWorker;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Strings;

public class RemoveForeignAnnotationsAction extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	public RemoveForeignAnnotationsAction(DocumentWindow dw) {
		super(dw, Strings.ACTION_REMOVE_FOREIGN_ANNOTATIONS, MaterialDesign.MDI_PHARMACY);
		putValue(Action.SHORT_DESCRIPTION, Strings.ACTION_REMOVE_FOREIGN_ANNOTATIONS_TOOLTIP);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().setIndeterminateProgress();
		new SwingWorker<Object, Object>() {

			@Override
			protected Object doInBackground() throws Exception {
				getTarget().getDocumentModel().removeForeignAnnotations();
				return null;
			}

			@Override
			protected void done() {
				getTarget().stopIndeterminateProgress();
			}
		}.execute();
	}

}
