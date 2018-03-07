package de.unistuttgart.ims.coref.annotator.comp;

import javax.swing.JPanel;

public interface PanelFactory<T, U extends JPanel> {
	public U getPanel(T object);
}
