package de.unistuttgart.ims.coref.annotator.comp;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import de.unistuttgart.ims.coref.annotator.Annotator;

public class PanelList<T, U extends JPanel> extends JPanel implements ListDataListener {

	private static final long serialVersionUID = 1L;

	ListModel<T> model;
	int selection;
	PanelFactory<T, U> factory;
	MutableMap<T, U> panelMap = Maps.mutable.empty();

	public PanelList(PanelFactory<T, U> pFactory) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.factory = pFactory;
		// this.setPreferredSize(new Dimension(200, 300));
	}

	public U getPanel(T obj) {
		if (!panelMap.containsKey(obj)) {
			panelMap.put(obj, factory.getPanel(obj));
		}
		return panelMap.get(obj);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void intervalAdded(ListDataEvent e) {
		Annotator.logger.debug("intervalAdded {}", e);
		for (int i = e.getIndex0(); i <= e.getIndex1(); i++) {
			add(getPanel(((ListModel<T>) e.getSource()).getElementAt(i)), i);
		}
		revalidate();
		repaint();
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		Annotator.logger.debug("intervalRemoved {}", e);
		for (int i = e.getIndex0(); i <= e.getIndex1(); i++) {
			Annotator.logger.debug("removing {}", i);
			this.remove(i);
		}
		repaint();
		revalidate();
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		repaint();
	}

	public ListModel<T> getModel() {
		return model;
	}

	public void setModel(ListModel<T> model) {
		this.model = model;
		model.addListDataListener(this);
	}

	public void setSelection(T c) {
		for (T t : panelMap.keySet()) {
			if (c == null || t == c)
				panelMap.get(t).setEnabled(true);
			else
				panelMap.get(t).setEnabled(false);
		}
		contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, model.getSize()));
	}

	public U get(T c) {
		return panelMap.get(c);
	}

}
