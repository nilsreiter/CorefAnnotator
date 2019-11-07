package de.unistuttgart.ims.coref.annotator.plugins;

import java.util.function.Consumer;

import javax.swing.JFrame;

public interface ConfigurableImportPlugin {
	void showImportConfigurationDialog(JFrame parent, Consumer<ConfigurableImportPlugin> callback);
}
