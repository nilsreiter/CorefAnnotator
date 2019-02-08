package de.unistuttgart.ims.coref.annotator.document;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.eclipse.collections.api.list.MutableList;

import de.unistuttgart.ims.coref.annotator.CoreferenceModelListener;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;

public class EntityComboBoxModel extends AbstractListModel<Entity>
		implements ModelAdapter, ComboBoxModel<Entity>, CoreferenceModelListener {

	private static final long serialVersionUID = 1L;
	MutableList<Entity> entityList;
	Entity selectedItem;

	@Override
	public int getSize() {
		return entityList.size();
	}

	@Override
	public Entity getElementAt(int index) {
		return entityList.get(index);
	}

	@Override
	public void entityEvent(FeatureStructureEvent event) {
		switch (event.getType()) {
		case Add:
			int startInterval = entityList.size();
			for (int i = 0; i < event.getArity(); i++) {
				if (event.getArgument(i) instanceof Entity)
					entityList.add((Entity) event.getArgument(i));
			}
			fireIntervalAdded(this, startInterval, startInterval + event.getArity());
			break;
		case Merge:
			break;
		case Op:
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
		default:
			break;

		}
	}

	@Override
	public void setSelectedItem(Object anItem) {
		selectedItem = (Entity) anItem;
	}

	@Override
	public Object getSelectedItem() {
		return selectedItem;
	}

}
