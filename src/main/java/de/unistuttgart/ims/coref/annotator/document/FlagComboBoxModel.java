package de.unistuttgart.ims.coref.annotator.document;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.apache.uima.cas.FeatureStructure;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;

public class FlagComboBoxModel extends AbstractListModel<Flag>
		implements ModelAdapter, ComboBoxModel<Flag>, FlagModelListener {

	private static final long serialVersionUID = 1L;
	MutableList<Flag> entityList;
	Entity selectedItem;
	Class<? extends FeatureStructure> targetClass;

	public FlagComboBoxModel(Class<? extends FeatureStructure> targetClass) {
		this.targetClass = targetClass;
	}

	@Override
	public int getSize() {
		return entityList.size();
	}

	@Override
	public Flag getElementAt(int index) {
		return entityList.get(index);
	}

	@Override
	public void flagEvent(FeatureStructureEvent event) {
		switch (event.getType()) {
		case Add:
			int startInterval = entityList.size();
			for (int i = 0; i < event.getArity(); i++) {
				if (event.getArgument(i) instanceof Entity)
					entityList.add((Flag) event.getArgument(i));
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
		case Init:
			entityList = Lists.mutable.withAll(((FlagModel) event.getSource()).getFlags(targetClass));
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
