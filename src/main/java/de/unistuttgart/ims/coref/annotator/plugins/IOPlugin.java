package de.unistuttgart.ims.coref.annotator.plugins;

import javax.swing.filechooser.FileFilter;

import javafx.stage.FileChooser.ExtensionFilter;

public interface IOPlugin extends Plugin {

	FileFilter getFileFilter();

	ExtensionFilter getExtensionFilter();

	String getSuffix();

	String[] getSupportedLanguages();

}
