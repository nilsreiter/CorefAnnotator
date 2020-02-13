package de.unistuttgart.ims.coref.annotator.plugin.statistics;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome.FontAwesome;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.ExtensionFilters;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.plugins.AbstractExportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.ConfigurableExportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.DocumentModelExportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.PluginConfigurationDialog;
import de.unistuttgart.ims.coref.annotator.plugins.PluginOption;
import de.unistuttgart.ims.coref.annotator.plugins.PluginOption.BooleanPluginOption;
import de.unistuttgart.ims.coref.annotator.stats.DocumentStatistics;
import javafx.stage.FileChooser.ExtensionFilter;

public class Plugin extends AbstractExportPlugin implements DocumentModelExportPlugin, ConfigurableExportPlugin {

	boolean includeHeader = true;

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
	public Ikon getIkon() {
		return FontAwesome.FILE_O;
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "Summary Statistics Export into CSV";
	}

	@Override
	public void write(DocumentModel documentModel, Appendable outputStream) {
		DocumentStatistics ds = new DocumentStatistics();
		ds.setDocumentModel(documentModel);

		try (CSVPrinter p = new CSVPrinter(outputStream, CSVFormat.DEFAULT)) {
			if (includeHeader)
				p.printRecord((Object[]) DocumentStatistics.Property.values());

			for (DocumentStatistics.Property property : DocumentStatistics.Property.values()) {
				p.print(ds.getValue(property));
			}
			p.println();
		} catch (IOException e) {
			Annotator.logger.catching(e);
		}
	}

	@Override
	public void showExportConfigurationDialog(JFrame parent, DocumentModel documentModel,
			Consumer<ConfigurableExportPlugin> callback) {
		ImmutableList<PluginOption> options = Lists.immutable.of(new BooleanPluginOption(Annotator.app.getPreferences(),
				Constants.PLUGIN_STATISTICS_INCLUDE_HEADER, Defaults.PLUGIN_STATISTICS_INCLUDE_HEADER,
				Strings.DIALOG_EXPORT_OPTIONS_INCLUDE_HEADER, Strings.DIALOG_EXPORT_OPTIONS_INCLUDE_HEADER_TOOLTIP));
		PluginConfigurationDialog pluginConfigurationDialog = new PluginConfigurationDialog(parent, this, callback,
				options);
		pluginConfigurationDialog.setVisible(true);
	}

}
