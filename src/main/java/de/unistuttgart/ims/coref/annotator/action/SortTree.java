package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.Action;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignS;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.EntitySortOrder;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.document.EntitySortOrderListener;

public class SortTree extends TargetedIkonAction<DocumentWindow>
		implements EntitySortOrderListener, PreferenceChangeListener {

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
		SortTree st = new SortTree(win, MaterialDesignS.SORT_ALPHABETICAL_ASCENDING, EntitySortOrder.Alphabet, false);
		st.putValue(Action.NAME, Annotator.getString(Strings.ACTION_SORT_ALPHA));
		st.putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_SORT_ALPHA_TOOLTIP));
		return st;
	}

	public static SortTree getSortByMention(DocumentWindow win) {
		SortTree st = new SortTree(win, MaterialDesignS.SORT_ASCENDING, EntitySortOrder.Mentions, true);
		st.putValue(Action.NAME, Annotator.getString(Strings.ACTION_SORT_MENTIONS));
		st.putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_SORT_MENTIONS_TOOLTIP));
		return st;
	}

	public static SortTree getSortByLastModified(DocumentWindow win) {
		SortTree st = new SortTree(win, MaterialDesignS.SORT_CLOCK_ASCENDING_OUTLINE, EntitySortOrder.LastModified,
				true);
		st.putValue(Action.NAME, Annotator.getString(Strings.ACTION_SORT_LASTMODIFIED));
		st.putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_SORT_LASTMODIFIED_TOOLTIP));
		return st;
	}

	public static SortTree getSortByPosition(DocumentWindow win) {
		SortTree st = new SortTree(win, MaterialDesignS.SORT_NUMERIC_ASCENDING, EntitySortOrder.Position, true);
		st.putValue(Action.NAME, Annotator.getString(Strings.ACTION_SORT_POSITION));
		st.putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_SORT_POSITION_TOOLTIP));
		return st;
	}

	@Override
	public void entitySortEvent(EntitySortOrder newOrder, boolean descending) {
		putValue(Action.SELECTED_KEY, newOrder == order);
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		if (getTarget().getDocumentModel().getTreeModel().getEntitySortOrder() == order
				&& evt.getKey() == Constants.CFG_KEEP_TREE_SORTED && evt.getNewValue() == Boolean.TRUE.toString())
			actionPerformed(null);
	}

}
