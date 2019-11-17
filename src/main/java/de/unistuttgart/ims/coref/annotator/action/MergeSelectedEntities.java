package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.document.op.MergeEntities;

public class MergeSelectedEntities extends TargetedOperationIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	public MergeSelectedEntities(DocumentWindow documentWindow) {
		super(documentWindow, Strings.ACTION_MERGE, MaterialDesign.MDI_CALL_MERGE);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_MERGE_TOOLTIP));
		operationClass = MergeEntities.class;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().getDocumentModel().edit(new MergeEntities(getTarget().getSelectedEntities()));

	}

}