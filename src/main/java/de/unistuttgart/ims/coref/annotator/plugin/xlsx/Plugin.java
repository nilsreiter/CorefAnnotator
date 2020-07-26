package de.unistuttgart.ims.coref.annotator.plugin.xlsx;

import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.filechooser.FileFilter;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.plugins.ConfigurableExportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.PluginConfigurationDialog;
import de.unistuttgart.ims.coref.annotator.plugins.PluginOption;
import de.unistuttgart.ims.coref.annotator.plugins.PluginOption.BooleanPluginOption;
import de.unistuttgart.ims.coref.annotator.plugins.UimaExportPlugin;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * 
 * @author reiterns
 */
public class Plugin extends de.unistuttgart.ims.coref.annotator.plugin.csv.CsvExportPlugin
		implements UimaExportPlugin, ConfigurableExportPlugin {

	public static final String XLSX = "xlsx";

	@Override
	public String getDescription() {
		return "Export mentions in an Excel file";
	}

	@Override
	public String getName() {
		return Annotator.getString(Strings.NAME_MS_EXCEL);
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
	public FileFilter getFileFilter() {
		return new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith("." + XLSX);
			}

			@Override
			public String getDescription() {
				return Annotator.getString(Strings.NAME_MS_EXCEL);
			}

		};
	}

	@Override
	public String getSuffix() {
		return "." + XLSX;
	}

	@Override
	public String[] getSupportedLanguages() {
		return de.unistuttgart.ims.coref.annotator.Constants.SUPPORTED_LANGUAGES;
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return new ExtensionFilter(Annotator.getString(Strings.NAME_MS_EXCEL), XLSX);
	}

	@Override
	public void showExportConfigurationDialog(JFrame parent, DocumentModel documentModel,
			Consumer<ConfigurableExportPlugin> callback) {

		ImmutableList<PluginOption> options = Lists.immutable.of(
				new PluginOption.IntegerPluginOption(Annotator.app.getPreferences(),
						Constants.PLUGIN_XLSX_CONTEXT_WIDTH, Defaults.CFG_OPTION_CONTEXT_WIDTH,
						Strings.DIALOG_EXPORT_OPTIONS_CONTEXT_WIDTH,
						Strings.DIALOG_EXPORT_OPTIONS_CONTEXT_WIDTH_TOOLTIP, 0, 500, 25),
				(PluginOption) new PluginOption.EnumPluginOption<ContextUnit>(ContextUnit.class,
						Annotator.app.getPreferences(), Constants.PLUGIN_XLSX_CONTEXT_UNIT,
						Defaults.CFG_OPTION_CONTEXT_UNIT, Strings.DIALOG_EXPORT_OPTIONS_CONTEXT_UNIT,
						Strings.DIALOG_EXPORT_OPTIONS_CONTEXT_UNIT_TOOLTIP, Lists.immutable.of(ContextUnit.values())
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
						Defaults.CFG_OPTION_TRIM, Strings.ACTION_TOGGLE_TRIM_WHITESPACE,
						Strings.ACTION_TOGGLE_TRIM_WHITESPACE_TOOLTIP),
				new BooleanPluginOption(Annotator.app.getPreferences(), Constants.PLUGIN_XLSX_REPLACE_NEWLINES,
						Defaults.CFG_OPTION_REPLACE_NEWLINES, Strings.DIALOG_EXPORT_OPTIONS_REPLACE_NEWLINE,
						Strings.DIALOG_EXPORT_OPTIONS_REPLACE_NEWLINE_TOOLTIP),
				new BooleanPluginOption(Annotator.app.getPreferences(), Constants.PLUGIN_XLSX_INCLUDE_LINE_NUMBERS,
						Defaults.CFG_OPTION_INCLUDE_LINE_NUMBERS, Strings.DIALOG_EXPORT_OPTIONS_INCLUDE_LINE_NUMBERS,
						Strings.DIALOG_EXPORT_OPTIONS_INCLUDE_LINE_NUMBERS_TOOLTIP),
				new BooleanPluginOption(Annotator.app.getPreferences(), Constants.PLUGIN_XLSX_SEPARATE_ENTITIES,
						Defaults.CFG_OPTION_SEPARATE_ENTITIES, Strings.DIALOG_EXPORT_OPTIONS_SEPARATE_ENTITIES,
						Strings.DIALOG_EXPORT_OPTIONS_SEPARATE_ENTITIES_TOOLTIP),
				new BooleanPluginOption(Annotator.app.getPreferences(), Constants.PLUGIN_XLSX_AUTO_OPEN,
						Defaults.CFG_OPTION_AUTO_OPEN, Strings.DIALOG_EXPORT_OPTIONS_AUTO_OPEN,
						Strings.DIALOG_EXPORT_OPTIONS_AUTO_OPEN_TOOLTIP));

		new PluginConfigurationDialog(parent, this, callback, options).setVisible(true);

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
				if (Annotator.app.getPreferences().getBoolean(Constants.PLUGIN_XLSX_AUTO_OPEN,
						Defaults.CFG_OPTION_AUTO_OPEN))
					Desktop.getDesktop().open(f);
			} catch (IOException e) {
				Annotator.logger.catching(e);
			}
		};
	}

	@Override
	public Ikon getIkon() {
		return MaterialDesign.MDI_FILE_EXCEL;
	}

}
