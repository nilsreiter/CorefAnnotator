package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.inspector.Inspector;

public class InspectDocument extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	public InspectDocument(DocumentWindow dw) {
		super(dw, "inspect.document", MaterialDesign.MDI_OWL);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Inspector insp = new Inspector(getTarget());
		insp.setVisible(true);
		insp.pack();
	}

}
