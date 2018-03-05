package de.unistuttgart.ims.coref.annotator.comp;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Util;

public class XFileChooser extends JFileChooser {

	private static final long serialVersionUID = 1L;

	JComboBox<String> languageSelector = new JComboBox<String>(Util.getSupportedLanguageNames());

	public XFileChooser() {
		super();
		JPanel languageSelectPanel = new JPanel();
		languageSelectPanel.setOpaque(false);
		languageSelectPanel.add(new JLabel(Annotator.getString(Constants.Strings.LANGUAGE) + ":"));
		languageSelectPanel.add(languageSelector);
		((java.awt.Container) ((java.awt.Container) getComponent(4)).getComponent(0)).add(languageSelectPanel);
	}

	public String getSelectedLanguage() {
		return (String) languageSelector.getSelectedItem();
	}

	public void setLanguages() {
		languageSelector.removeAllItems();
		for (String l : Util.getSupportedLanguageNames())
			languageSelector.addItem(l);
	}

	public void setLanguages(String[] s) {
		languageSelector.removeAllItems();
		for (String str : s)
			languageSelector.addItem(Util.getLanguageName(str));
	};

}
