package de.unistuttgart.ims.coref.annotator.plugin.csv;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.ExtensionFilters;
import de.unistuttgart.ims.coref.annotator.HelpWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.plugins.ConfigurableExportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * 
 * @author reiterns TODO: make configurable
 */
public class Plugin implements IOPlugin, ConfigurableExportPlugin {

	int optionContextWidth = 0;
	boolean optionTrimWhitespace = true;
	boolean optionReplaceNewlines = true;

	@Override
	public String getDescription() {
		return "Export mentions in a CSV table";
	}

	@Override
	public String getName() {
		return "CSV";
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		return null;
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(NoOpAnnotator.class);
	}

	@Override
	public AnalysisEngineDescription getWriter(File f) throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(CSVWriter.class, CSVWriter.PARAM_FILE, f.getAbsolutePath(),
				CSVWriter.PARAM_CONTEXTWIDTH, optionContextWidth, CSVWriter.PARAM_REPLACE_NEWLINES,
				optionReplaceNewlines, CSVWriter.PARAM_TRIM_WHITESPACE, optionTrimWhitespace));
		return b.createAggregateDescription();
	}

	@Override
	public CollectionReaderDescription getReader(File f) throws ResourceInitializationException {
		return null;
	}

	@Override
	public Class<? extends StylePlugin> getStylePlugin() {
		return null;
	}

	@Override
	public FileFilter getFileFilter() {
		return new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".csv");
			}

			@Override
			public String getDescription() {
				return "CSV";
			}

		};
	}

	@Override
	public String getSuffix() {
		return ".csv";
	}

	@Override
	public String[] getSupportedLanguages() {
		return de.unistuttgart.ims.coref.annotator.Constants.SUPPORTED_LANGUAGES;
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return ExtensionFilters.csv;
	}

	@Override
	public void showExportConfigurationDialog(JFrame parent, Consumer<ConfigurableExportPlugin> callback) {
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(optionContextWidth, 0, 500, 25));
		JCheckBox trimWhitespace = new JCheckBox();
		JCheckBox replaceNewlineCharacters = new JCheckBox();
		trimWhitespace.setSelected(optionTrimWhitespace);

		JDialog dialog = new JDialog(parent, Annotator.getString(Strings.DIALOG_EXPORT_OPTIONS_TITLE));

		JPanel optionPanel = new JPanel(new GridLayout(0, 2));
		optionPanel.add(getLabel(Annotator.getString("dialog.export_options.context_width"),
				Annotator.getString("dialog.export_options.context_width.tooltip")));
		optionPanel.add(spinner);

		optionPanel.add(getLabel(Annotator.getString("dialog.export_options.trim_whitespace"),
				Annotator.getString("dialog.export_options.trim_whitespace.tooltip")));
		optionPanel.add(trimWhitespace);

		optionPanel.add(getLabel(Annotator.getString("dialog.export_options.replace_newline"),
				Annotator.getString("dialog.export_options.replace_newline.tooltip")));
		optionPanel.add(replaceNewlineCharacters);

		optionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		Action okAction = new AbstractAction(Annotator.getString(Strings.DIALOG_EXPORT_OPTIONS_OK)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				optionContextWidth = ((SpinnerNumberModel) spinner.getModel()).getNumber().intValue();
				optionTrimWhitespace = trimWhitespace.isSelected();
				optionReplaceNewlines = replaceNewlineCharacters.isSelected();
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

	public int getOptionContextWidth() {
		return optionContextWidth;
	}

	public void setOptionContextWidth(int optionContextWidth) {
		this.optionContextWidth = optionContextWidth;
	}

	public boolean isOptionTrimWhitespace() {
		return optionTrimWhitespace;
	}

	public void setOptionTrimWhitespace(boolean optionTrimWhitespace) {
		this.optionTrimWhitespace = optionTrimWhitespace;
	}

	public boolean isOptionReplaceNewlines() {
		return optionReplaceNewlines;
	}

	public void setOptionReplaceNewlines(boolean optionReplaceNewlines) {
		this.optionReplaceNewlines = optionReplaceNewlines;
	}

}
