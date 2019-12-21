package de.unistuttgart.ims.coref.annotator.plugin.xlsx;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
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
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.eclipse.collections.impl.factory.Lists;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.HelpWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.api.v1.Line;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.plugins.ConfigurableExportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * 
 * @author reiterns TODO: make configurable
 */
public class Plugin implements IOPlugin, ConfigurableExportPlugin {

	public static enum ContextUnit {
		CHARACTER, TOKEN, LINE;

		public boolean isPossible(JCas jcas) {
			switch (this) {
			case LINE:
				return JCasUtil.exists(jcas, Line.class);
			case TOKEN:
				return JCasUtil.exists(jcas, Token.class);
			default:
				return true;
			}
		}
	};

	@Override
	public String getDescription() {
		return "Export mentions in an Excel file";
	}

	@Override
	public String getName() {
		return "Microsoft Excel";
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
		b.add(AnalysisEngineFactory.createEngineDescription(XLSXWriter.class, XLSXWriter.PARAM_FILE,
				f.getAbsolutePath(), XLSXWriter.PARAM_CONTEXTWIDTH, getOptionContextWidth(),
				XLSXWriter.PARAM_REPLACE_NEWLINES, isOptionReplaceNewlines(), XLSXWriter.PARAM_TRIM_WHITESPACE,
				isOptionTrimWhitespace(), XLSXWriter.PARAM_CONTEXT_UNIT, getOptionContextUnit(),
				XLSXWriter.PARAM_INCLUDE_LINE_NUMBERS, Annotator.app.getPreferences().getBoolean(
						Constants.PLUGIN_XLSX_INCLUDE_LINE_NUMBERS, Defaults.CFG_OPTION_INCLUDE_LINE_NUMBERS)));
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
				return f.getName().endsWith(".xlsx");
			}

			@Override
			public String getDescription() {
				return "Microsoft Excel";
			}

		};
	}

	@Override
	public String getSuffix() {
		return ".xlsx";
	}

	@Override
	public String[] getSupportedLanguages() {
		return de.unistuttgart.ims.coref.annotator.Constants.SUPPORTED_LANGUAGES;
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return new ExtensionFilter("Microsoft Excel", "xlsx");
	}

	@Override
	public void showExportConfigurationDialog(JFrame parent, DocumentModel documentModel,
			Consumer<ConfigurableExportPlugin> callback) {

		JSpinner spinner = new JSpinner(new SpinnerNumberModel(
				Annotator.app.getPreferences().getInt((Constants.PLUGIN_XLSX_CONTEXT_WIDTH), 0), 0, 500, 25));
		JCheckBox trimWhitespace = new JCheckBox();
		JCheckBox replaceNewlineCharacters = new JCheckBox();
		JCheckBox includeLineNumbers = new JCheckBox();
		JComboBox<ContextUnit> contextUnitBox = new JComboBox<ContextUnit>(Lists.immutable.of(ContextUnit.values())
				.select(cu -> cu.isPossible(documentModel.getJcas())).toArray(new ContextUnit[] {}));
		contextUnitBox.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				setText(Annotator.getString("dialog.export_options.context_unit." + value.toString()));
				return this;
			}
		});

		contextUnitBox.setSelectedItem(ContextUnit.valueOf(Annotator.app.getPreferences()
				.get(Constants.PLUGIN_XLSX_CONTEXT_UNIT, Defaults.CFG_OPTION_CONTEXT_UNIT.name())));
		trimWhitespace.setSelected(
				Annotator.app.getPreferences().getBoolean(Constants.PLUGIN_XLSX_TRIM, Defaults.CFG_OPTION_TRIM));
		replaceNewlineCharacters.setSelected(Annotator.app.getPreferences()
				.getBoolean(Constants.PLUGIN_XLSX_REPLACE_NEWLINES, Defaults.CFG_OPTION_REPLACE_NEWLINES));
		includeLineNumbers.setSelected(Annotator.app.getPreferences()
				.getBoolean(Constants.PLUGIN_XLSX_INCLUDE_LINE_NUMBERS, Defaults.CFG_OPTION_INCLUDE_LINE_NUMBERS));

		JDialog dialog = new JDialog(parent, Annotator.getString(Strings.DIALOG_EXPORT_OPTIONS_TITLE));

		JPanel optionPanel = new JPanel(new GridLayout(0, 2));
		optionPanel.add(getLabel(Annotator.getString("dialog.export_options.context_width"),
				Annotator.getString("dialog.export_options.context_width.tooltip")));
		optionPanel.add(spinner);

		optionPanel.add(getLabel(Annotator.getString("dialog.export_options.context_unit"),
				Annotator.getString("dialog.export_options.context_unit.tooltip")));
		optionPanel.add(contextUnitBox);

		optionPanel.add(getLabel(Annotator.getString("dialog.export_options.trim_whitespace"),
				Annotator.getString("dialog.export_options.trim_whitespace.tooltip")));
		optionPanel.add(trimWhitespace);

		optionPanel.add(getLabel(Annotator.getString("dialog.export_options.replace_newline"),
				Annotator.getString("dialog.export_options.replace_newline.tooltip")));
		optionPanel.add(replaceNewlineCharacters);

		optionPanel.add(getLabel(Annotator.getString("dialog.export_options.include_line_numbers"),
				Annotator.getString("dialog.export_options.include_line_numbers.tooltip")));
		optionPanel.add(includeLineNumbers);

		optionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		Action okAction = new AbstractAction(Annotator.getString(Strings.DIALOG_EXPORT_OPTIONS_OK)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Annotator.app.getPreferences().put(Constants.PLUGIN_XLSX_CONTEXT_UNIT,
						((ContextUnit) contextUnitBox.getSelectedItem()).name());
				Annotator.app.getPreferences().putInt(Constants.PLUGIN_XLSX_CONTEXT_WIDTH,
						((SpinnerNumberModel) spinner.getModel()).getNumber().intValue());
				Annotator.app.getPreferences().putBoolean(Constants.PLUGIN_XLSX_TRIM, trimWhitespace.isSelected());
				Annotator.app.getPreferences().putBoolean(Constants.PLUGIN_XLSX_REPLACE_NEWLINES,
						replaceNewlineCharacters.isSelected());
				Annotator.app.getPreferences().putBoolean(Constants.PLUGIN_XLSX_INCLUDE_LINE_NUMBERS,
						includeLineNumbers.isSelected());
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
		return Annotator.app.getPreferences().getInt((Constants.PLUGIN_XLSX_CONTEXT_WIDTH), 30);
	}

	public boolean isOptionTrimWhitespace() {
		return Annotator.app.getPreferences().getBoolean((Constants.PLUGIN_XLSX_TRIM), true);
	}

	public boolean isOptionReplaceNewlines() {
		return Annotator.app.getPreferences().getBoolean((Constants.PLUGIN_XLSX_REPLACE_NEWLINES), true);
	}

	public ContextUnit getOptionContextUnit() {
		return ContextUnit.valueOf(
				Annotator.app.getPreferences().get((Constants.PLUGIN_XLSX_CONTEXT_UNIT), ContextUnit.CHARACTER.name()));
	}

	@Override
	public Consumer<File> getPostExportAction() {
		return f -> {
			try {
				Desktop.getDesktop().open(f);
			} catch (IOException e) {
				Annotator.logger.catching(e);
			}
		};
	}

}
