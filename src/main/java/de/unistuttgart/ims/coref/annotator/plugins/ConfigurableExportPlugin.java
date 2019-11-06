package de.unistuttgart.ims.coref.annotator.plugins;

import java.util.function.Consumer;

import javax.swing.JFrame;

public interface ConfigurableExportPlugin {
	void showExportConfigurationDialog(JFrame parent, Consumer<ConfigurableExportPlugin> callback);

}
