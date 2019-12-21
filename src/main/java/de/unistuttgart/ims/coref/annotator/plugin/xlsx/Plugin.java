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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.HelpWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.plugins.ConfigurableExportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.PluginOption;
import de.unistuttgart.ims.coref.annotator.plugins.PluginOption.BooleanPluginOption;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * 
 * @author reiterns TODO: make configurable
 */
public class Plugin extends de.unistuttgart.ims.coref.annotator.plugin.csv.Plugin
		implements IOPlugin, ConfigurableExportPlugin {

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
		b.add(AnalysisEngineFactory.createEngineDescription(XLSXWriter.class,
				XLSXWriter.PARAM_SEPARATE_SHEETS_FOR_ENTITIES,
				Annotator.app.getPreferences().getBoolean(Constants.PLUGIN_XLSX_SEPARATE_ENTITIES,
						Defaults.CFG_OPTION_SEPARATE_ENTITIES),
				XLSXWriter.PARAM_FILE, f.getAbsolutePath(), XLSXWriter.PARAM_CONTEXTWIDTH, getOptionContextWidth(),
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

		ImmutableList<PluginOption> options = Lists.immutable.of(new PluginOption.IntegerPluginOption(
				Annotator.app.getPreferences(), Constants.PLUGIN_XLSX_CONTEXT_WIDTH, Defaults.CFG_OPTION_CONTEXT_WIDTH,
				"dialog.export_options.context_width", "dialog.export_options.context_width.tooltip", 0, 500, 25),
				(PluginOption) new PluginOption.EnumPluginOption<ContextUnit>(ContextUnit.class,
						Annotator.app.getPreferences(), Constants.PLUGIN_XLSX_CONTEXT_UNIT,
						Defaults.CFG_OPTION_CONTEXT_UNIT, "dialog.export_options.context_unit",
						"dialog.export_options.context_unit.tooltip", Lists.immutable.of(ContextUnit.values())
								.select(cu -> cu.isPossible(documentModel.getJcas())).toArray(new ContextUnit[] {}),
						new DefaultListCellRenderer() {
							private static final long serialVersionUID = 1L;

							@Override
							public Component getListCellRendererComponent(JList<?> list, Object value, int index,
									boolean isSelected, boolean cellHasFocus) {
								super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
								setText(Annotator.getString("dialog.export_options.context_unit." + value.toString()));
								return this;
							}
						}),
				new BooleanPluginOption(Annotator.app.getPreferences(), Constants.PLUGIN_XLSX_TRIM,
						Defaults.CFG_OPTION_TRIM, "dialog.export_options.trim_whitespace",
						"dialog.export_options.trim_whitespace.tooltip"),
				new BooleanPluginOption(Annotator.app.getPreferences(), Constants.PLUGIN_XLSX_REPLACE_NEWLINES,
						Defaults.CFG_OPTION_REPLACE_NEWLINES, "dialog.export_options.replace_newline",
						"dialog.export_options.replace_newline.tooltip"),
				new BooleanPluginOption(Annotator.app.getPreferences(), Constants.PLUGIN_XLSX_INCLUDE_LINE_NUMBERS,
						Defaults.CFG_OPTION_INCLUDE_LINE_NUMBERS, "dialog.export_options.include_line_numbers",
						"dialog.export_options.include_line_numbers.tooltip"),
				new BooleanPluginOption(Annotator.app.getPreferences(), Constants.PLUGIN_XLSX_SEPARATE_ENTITIES,
						Defaults.CFG_OPTION_SEPARATE_ENTITIES, "dialog.export_options.separate_entities",
						"dialog.export_options.separate_entities.tooltip"));

		JDialog dialog = new JDialog(parent, Annotator.getString(Strings.DIALOG_EXPORT_OPTIONS_TITLE));

		JPanel optionPanel = new JPanel(new GridLayout(0, 2));

		for (PluginOption option : options) {
			optionPanel.add(option.getLabel());
			optionPanel.add(option.getComponent());
		}

		optionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		Action okAction = new AbstractAction(Annotator.getString(Strings.DIALOG_EXPORT_OPTIONS_OK)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				for (PluginOption option : options)
					option.ok();

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

	@Override
	protected JLabel getLabel(String text, String tooltip) {
		JLabel lab = new JLabel(text);
		lab.setToolTipText(tooltip);
		return lab;
	}

	@Override
	public int getOptionContextWidth() {
		return Annotator.app.getPreferences().getInt((Constants.PLUGIN_XLSX_CONTEXT_WIDTH), 30);
	}

	@Override
	public boolean isOptionTrimWhitespace() {
		return Annotator.app.getPreferences().getBoolean((Constants.PLUGIN_XLSX_TRIM), true);
	}

	@Override
	public boolean isOptionReplaceNewlines() {
		return Annotator.app.getPreferences().getBoolean((Constants.PLUGIN_XLSX_REPLACE_NEWLINES), true);
	}

	@Override
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
