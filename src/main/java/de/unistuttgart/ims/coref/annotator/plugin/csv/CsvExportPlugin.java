package de.unistuttgart.ims.coref.annotator.plugin.csv;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.ExtensionFilters;
import de.unistuttgart.ims.coref.annotator.HelpWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.api.v1.Line;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.plugins.AbstractExportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.ConfigurableExportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.PluginOption;
import de.unistuttgart.ims.coref.annotator.plugins.PluginOption.BooleanPluginOption;
import de.unistuttgart.ims.coref.annotator.plugins.UimaExportPlugin;
import javafx.stage.FileChooser.ExtensionFilter;

public class CsvExportPlugin extends AbstractExportPlugin implements UimaExportPlugin, ConfigurableExportPlugin {

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
		return "Export mentions in a CSV table";
	}

	@Override
	public String getName() {
		return "CSV";
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(NoOpAnnotator.class);
	}

	@Override
	public AnalysisEngineDescription getWriter(File f) throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(CSVWriter.class, CSVWriter.PARAM_FILE, f.getAbsolutePath(),
				CSVWriter.PARAM_CONTEXTWIDTH, getOptionContextWidth(), CSVWriter.PARAM_REPLACE_NEWLINES,
				isOptionReplaceNewlines(), CSVWriter.PARAM_TRIM_WHITESPACE, isOptionTrimWhitespace(),
				CSVWriter.PARAM_CONTEXT_UNIT, getOptionContextUnit(), CSVWriter.PARAM_INCLUDE_LINE_NUMBERS,
				Annotator.app.getPreferences().getBoolean(Constants.PLUGIN_CSV_INCLUDE_LINE_NUMBERS,
						Defaults.CFG_OPTION_INCLUDE_LINE_NUMBERS)));
		return b.createAggregateDescription();
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
	public ExtensionFilter getExtensionFilter() {
		return ExtensionFilters.csv;
	}

	@Override
	public void showExportConfigurationDialog(JFrame parent, DocumentModel documentModel,
			Consumer<ConfigurableExportPlugin> callback) {

		ImmutableList<PluginOption> options = Lists.immutable.of(
				new PluginOption.IntegerPluginOption(Annotator.app.getPreferences(), Constants.PLUGIN_CSV_CONTEXT_WIDTH,
						Defaults.CFG_OPTION_CONTEXT_WIDTH, "dialog.export_options.context_width",
						"dialog.export_options.context_width.tooltip", 0, 500, 25),
				(PluginOption) new PluginOption.EnumPluginOption<ContextUnit>(ContextUnit.class,
						Annotator.app.getPreferences(), Constants.PLUGIN_CSV_CONTEXT_UNIT,
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
				new BooleanPluginOption(Annotator.app.getPreferences(), Constants.PLUGIN_CSV_TRIM,
						Defaults.CFG_OPTION_TRIM, "dialog.export_options.trim_whitespace",
						"dialog.export_options.trim_whitespace.tooltip"),
				new BooleanPluginOption(Annotator.app.getPreferences(), Constants.PLUGIN_CSV_REPLACE_NEWLINES,
						Defaults.CFG_OPTION_REPLACE_NEWLINES, "dialog.export_options.replace_newline",
						"dialog.export_options.replace_newline.tooltip"),
				new BooleanPluginOption(Annotator.app.getPreferences(), Constants.PLUGIN_CSV_INCLUDE_LINE_NUMBERS,
						Defaults.CFG_OPTION_INCLUDE_LINE_NUMBERS, "dialog.export_options.include_line_numbers",
						"dialog.export_options.include_line_numbers.tooltip"));

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
				callback.accept(CsvExportPlugin.this);
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

	public int getOptionContextWidth() {
		return Annotator.app.getPreferences().getInt((Constants.PLUGIN_CSV_CONTEXT_WIDTH), 30);
	}

	public boolean isOptionTrimWhitespace() {
		return Annotator.app.getPreferences().getBoolean((Constants.PLUGIN_CSV_TRIM), true);
	}

	public boolean isOptionReplaceNewlines() {
		return Annotator.app.getPreferences().getBoolean((Constants.PLUGIN_CSV_REPLACE_NEWLINES), true);
	}

	public ContextUnit getOptionContextUnit() {
		return ContextUnit.valueOf(
				Annotator.app.getPreferences().get((Constants.PLUGIN_CSV_CONTEXT_UNIT), ContextUnit.CHARACTER.name()));
	}

}
