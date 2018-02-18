package de.unistuttgart.ims.coref.annotator.comp;

import javax.swing.JPanel;

public interface PanelFactory<T> {
	public JPanel getPanel(T object);
}
