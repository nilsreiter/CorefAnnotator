package de.unistuttgart.ims.coref.annotator.plugins;

import java.io.File;
import java.util.function.Consumer;

import javax.swing.filechooser.FileFilter;

import org.kordamp.ikonli.Ikon;

import javafx.stage.FileChooser.ExtensionFilter;

public interface IOPlugin extends Plugin {
	Class<? extends StylePlugin> getStylePlugin();

	FileFilter getFileFilter();

	ExtensionFilter getExtensionFilter();

	String getSuffix();

	String[] getSupportedLanguages();

	Consumer<File> getPostExportAction();

	Ikon getIkon();

}
