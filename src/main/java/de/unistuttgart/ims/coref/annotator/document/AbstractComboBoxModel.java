package de.unistuttgart.ims.coref.annotator.document;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.eclipse.collections.api.list.MutableList;

public class AbstractComboBoxModel<E> extends AbstractListModel<E> implements ModelAdapter, ComboBoxModel<E> {

	private static final long serialVersionUID = 1L;

	MutableList<E> entityList;

	E selectedItem;

	@Override
	public int getSize() {
		return entityList.size();
	}

	@Override
	public E getElementAt(int index) {
		return entityList.get(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSelectedItem(Object anItem) {
		selectedItem = (E) anItem;
	}

	@Override
	public Object getSelectedItem() {
		return selectedItem;
	}

}
