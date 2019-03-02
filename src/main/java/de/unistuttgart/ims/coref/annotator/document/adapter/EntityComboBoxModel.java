package de.unistuttgart.ims.coref.annotator.document.adapter;

import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel.EntitySorter;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModelListener;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;

public class EntityComboBoxModel extends AbstractComboBoxModel<Entity> implements CoreferenceModelListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void entityEvent(FeatureStructureEvent event) {
		switch (event.getType()) {
		case Add:
			for (int i = 1; i < event.getArity(); i++) {
				if (event.getArgument(i) instanceof Entity) {
					entityList.add((Entity) event.getArgument(i));
					fireIntervalAdded(this, entityList.size() + i, entityList.size() + i);
				}
			}
			break;
		case Merge:
			break;
		case Remove:
			for (int i = 0; i < event.getArity(); i++) {
				if (event.getArgument(i) instanceof Entity) {
					int index = entityList.indexOf(event.getArgument(i));
					fireIntervalRemoved(this, index, index);
				}
			}
			break;
		case Update:
			break;
		case Init:
			entityList = Lists.mutable
					.withAll(((CoreferenceModel) event.getSource()).getEntities(EntitySorter.CHILDREN));
			selectedItem = null;
			break;
		default:
			break;

		}
	}

}
