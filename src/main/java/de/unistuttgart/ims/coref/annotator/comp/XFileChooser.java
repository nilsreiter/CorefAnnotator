package de.unistuttgart.ims.coref.annotator.comp;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class XFileChooser extends JFileChooser {

	private static final long serialVersionUID = 1L;

	JComboBox<String> languageSelector = new JComboBox<String>(new String[] { "de", "en" });

	public XFileChooser() {
		super();
		JPanel languageSelectPanel = new JPanel();
		languageSelectPanel.add(new JLabel("Language"));
		languageSelectPanel.add(languageSelector);
		((java.awt.Container) getComponent(4)).add(languageSelectPanel, 1);
	}

	public String getSelectedLanguage() {
		return (String) languageSelector.getSelectedItem();
	}
}
