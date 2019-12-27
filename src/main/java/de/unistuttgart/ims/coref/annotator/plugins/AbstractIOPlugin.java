package de.unistuttgart.ims.coref.annotator.plugins;

import javax.swing.filechooser.FileFilter;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import javafx.stage.FileChooser.ExtensionFilter;

@Deprecated
public abstract class AbstractIOPlugin implements IOPlugin {

	@Override
	public FileFilter getFileFilter() {
		return null;
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return null;
	}

	@Override
	public String getSuffix() {
		return null;
	}

	@Override
	public String[] getSupportedLanguages() {
		return de.unistuttgart.ims.coref.annotator.Constants.SUPPORTED_LANGUAGES;
	}

	@Override
	public Ikon getIkon() {
		return MaterialDesign.MDI_FILE_EXPORT;
	}

}
