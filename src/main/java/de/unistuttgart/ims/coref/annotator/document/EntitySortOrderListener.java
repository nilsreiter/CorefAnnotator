package de.unistuttgart.ims.coref.annotator.document;

import de.unistuttgart.ims.coref.annotator.EntitySortOrder;

public interface EntitySortOrderListener {
	void entitySortEvent(EntitySortOrder newOrder, boolean descending);
}
