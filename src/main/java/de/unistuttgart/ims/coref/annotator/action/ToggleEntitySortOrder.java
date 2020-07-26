package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.EntitySortOrder;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.document.EntitySortOrderListener;

public class ToggleEntitySortOrder extends TargetedIkonAction<DocumentWindow> implements EntitySortOrderListener {

	private static final long serialVersionUID = 1L;

	public ToggleEntitySortOrder(DocumentWindow dw) {
		super(dw, Strings.ACTION_SORT_REVERT, MaterialDesign.MDI_SORT_VARIANT);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().getDocumentModel().getTreeModel().getEntitySortOrder().descending = !getTarget().getDocumentModel()
				.getTreeModel().getEntitySortOrder().descending;
		getTarget().getDocumentModel().getTreeModel().resort();
	}

	@Override
	public void entitySortEvent(EntitySortOrder newOrder, boolean descending) {
		this.putValue(SELECTED_KEY, descending);

	}
}