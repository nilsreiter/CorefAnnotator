package de.unistuttgart.ims.coref.annotator.plugins;

import java.io.File;
import java.util.function.Consumer;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Constants;

public abstract class AbstractExportPlugin implements ExportPlugin {

	@Override
	public String[] getSupportedLanguages() {
		return Constants.SUPPORTED_LANGUAGES;
	}

	@Override
	public Ikon getIkon() {
		return MaterialDesign.MDI_FILE_EXPORT;
	}

	@Override
	public Consumer<File> getPostExportAction() {
		return null;
	}

}
