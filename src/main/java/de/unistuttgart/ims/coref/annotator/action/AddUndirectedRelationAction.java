package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.op.AddUndirectedRelation;

public class AddUndirectedRelationAction extends TargetedIkonAction<DocumentModel> {

	private static final long serialVersionUID = 1L;

	public AddUndirectedRelationAction(DocumentModel dw) {
		super(dw, Constants.Strings.ACTION_ADD_UNDIRECTED_RELATION, MaterialDesign.MDI_FLAG);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_ADD_UNDIRECTED_RELATION_TOOLTIP));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().edit(new AddUndirectedRelation());
	}

}
