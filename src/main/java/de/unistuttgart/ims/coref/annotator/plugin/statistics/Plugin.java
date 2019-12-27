package de.unistuttgart.ims.coref.annotator.plugin.statistics;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome.FontAwesome;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.ExtensionFilters;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.plugins.AbstractExportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.DocumentModelExportPlugin;
import de.unistuttgart.ims.coref.annotator.stats.DocumentStatistics;
import javafx.stage.FileChooser.ExtensionFilter;

public class Plugin extends AbstractExportPlugin implements DocumentModelExportPlugin {

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
			p.printRecord((Object[]) DocumentStatistics.Property.values());
			for (DocumentStatistics.Property property : DocumentStatistics.Property.values()) {
				p.print(ds.getValue(property));
			}
			p.println();
		} catch (IOException e) {
			Annotator.logger.catching(e);
		}
	}

}
