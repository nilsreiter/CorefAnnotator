package de.unistuttgart.ims.coref.annotator.plugins;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Constants;

public abstract class AbstractImportPlugin implements ImportPlugin {

	@Override
	public String[] getSupportedLanguages() {
		return Constants.SUPPORTED_LANGUAGES;
	}

	@Override
	public Ikon getIkon() {
		return MaterialDesign.MDI_FILE_IMPORT;
	}

	@Override
	public Class<? extends StylePlugin> getStylePlugin() {
		return null;
	}

}
