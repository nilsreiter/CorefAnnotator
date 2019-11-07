package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.EntitySortOrder;
import de.unistuttgart.ims.coref.annotator.document.EntitySortOrderListener;

public class SortTree extends TargetedIkonAction<DocumentWindow> implements EntitySortOrderListener {

	private static final long serialVersionUID = 1L;
	EntitySortOrder order = EntitySortOrder.Mentions;
	boolean descending = true;

	private SortTree(DocumentWindow dw, Ikon ikon, EntitySortOrder order, boolean descending) {
		super(dw, ikon);
		this.order = order;
		this.descending = descending;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getTarget().getDocumentModel().getTreeModel().setEntitySortOrder(order);
		getTarget().getDocumentModel().getTreeModel().getEntitySortOrder().descending = descending;
		getTarget().getDocumentModel().getTreeModel().resort();
		putValue(Action.SELECTED_KEY, true);
	}

	public static SortTree getSortByAlphabet(DocumentWindow win) {
		return new SortTree(win, MaterialDesign.MDI_SORT_ALPHABETICAL, EntitySortOrder.Alphabet, false);
	}

	public static SortTree getSortByMention(DocumentWindow win) {
		return new SortTree(win, MaterialDesign.MDI_SORT_NUMERIC, EntitySortOrder.Mentions, true);
	}

	@Override
	public void entitySortEvent(EntitySortOrder newOrder, boolean descending) {
		putValue(Action.SELECTED_KEY, newOrder == order);
	}

}
