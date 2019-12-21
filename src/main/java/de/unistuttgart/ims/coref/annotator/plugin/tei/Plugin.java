package de.unistuttgart.ims.coref.annotator.plugin.tei;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.ExtensionFilters;
import de.unistuttgart.ims.coref.annotator.FileFilters;
import de.unistuttgart.ims.coref.annotator.HelpWindow;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.plugins.ConfigurableImportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;
import de.unistuttgart.ims.coref.annotator.uima.EnsureMeta;
import javafx.stage.FileChooser.ExtensionFilter;

public class Plugin implements ConfigurableImportPlugin, IOPlugin {

	String language = Constants.X_UNSPECIFIED;
	String textRootSelector = null;

	ResourceBundle resourceBundle;

	public Plugin() {
		resourceBundle = ResourceBundle.getBundle("plugins.tei.strings", Locale.getDefault());
	}

	@Override
	public String getDescription() {
		try {
			return IOUtils.toString(getClass().getResourceAsStream("/plugins/tei/description.txt"), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public String getName() {
		return "TEI/XML";
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(EnsureMeta.class));
		return b.createAggregateDescription();
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(MapCorefToXmlElements.class);
	}

	@Override
	public CollectionReaderDescription getReader(File f) throws ResourceInitializationException {
		return CollectionReaderFactory.createReaderDescription(TeiReader.class, TeiReader.PARAM_SOURCE_LOCATION,
				f.getAbsoluteFile(), TeiReader.PARAM_TEXT_ROOT_SELECTOR, textRootSelector, TeiReader.PARAM_LANGUAGE,
				language, TeiReader.PARAM_DOCUMENT_ID, f.getName());
	}

	@Override
	public AnalysisEngineDescription getWriter(File f) throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(TeiWriter.class, TeiWriter.PARAM_OUTPUT_FILE,
				f.getAbsolutePath());
	}

	@Override
	public Class<? extends StylePlugin> getStylePlugin() {
		return TeiStylePlugin.class;
	}

	@Override
	public FileFilter getFileFilter() {
		return FileFilters.tei;
	}

	@Override
	public String getSuffix() {
		return ".xml";
	}

	@Override
	public String[] getSupportedLanguages() {
		return null;
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return ExtensionFilters.tei;
	}

	@Override
	public void showImportConfigurationDialog(JFrame parent, Consumer<ConfigurableImportPlugin> callback) {

		JTextField rootSelectorInput = new JTextField();

		JComboBox<String> languageDropdown = new JComboBox<String>();
		for (int i = 0; i < Util.getSupportedLanguageNames().length; i++) {
			String l = Util.getSupportedLanguageNames()[i];
			languageDropdown.addItem(l);
			if (l == Constants.X_UNSPECIFIED)
				languageDropdown.setSelectedIndex(i);

		}

		JDialog dialog = new JDialog(parent,
				resourceBundle.getString(de.unistuttgart.ims.coref.annotator.plugin.tei.Strings.IMPORT_DIALOG_TITLE));

		Action okAction = new AbstractAction(
				resourceBundle.getString(de.unistuttgart.ims.coref.annotator.plugin.tei.Strings.IMPORT_DIALOG_OK)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				textRootSelector = rootSelectorInput.getText();
				language = Util.getLanguage((String) languageDropdown.getSelectedItem());
				if (language == null)
					language = Constants.X_UNSPECIFIED;

				dialog.dispose();
				callback.accept(Plugin.this);
			}
		};

		Action cancelAction = new AbstractAction(
				Annotator.getString(de.unistuttgart.ims.coref.annotator.Strings.DIALOG_CANCEL)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		};

		Action helpAction = new AbstractAction(
				Annotator.getString(de.unistuttgart.ims.coref.annotator.Strings.MENU_HELP)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				HelpWindow.show("Input/Output");
			}
		};

		JPanel optionPanel = new JPanel(new GridLayout(0, 2));
		optionPanel.add(getLabel(resourceBundle.getString(Strings.IMPORT_DIALOG_ROOT_SELECTOR),
				resourceBundle.getString(Strings.IMPORT_DIALOG_ROOT_SELECTOR_TOOLTIP)));
		optionPanel.add(rootSelectorInput);

		optionPanel.add(getLabel(Annotator.getString(de.unistuttgart.ims.coref.annotator.Strings.LANGUAGE),
				resourceBundle.getString(Strings.IMPORT_DIALOG_LANGUAGE_TOOLTIP)));
		optionPanel.add(languageDropdown);
		optionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		JButton okButton = new JButton(okAction);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.add(new JButton(cancelAction));
		buttonPanel.add(new JButton(helpAction));

		dialog.getContentPane().add(optionPanel, BorderLayout.CENTER);
		dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		SwingUtilities.getRootPane(okButton).setDefaultButton(okButton);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	protected JLabel getLabel(String text, String tooltip) {
		JLabel lab = new JLabel(text);
		lab.setToolTipText(tooltip);
		return lab;
	}

	@Override
	public Consumer<File> getPostExportAction() {
		return null;
	}

}