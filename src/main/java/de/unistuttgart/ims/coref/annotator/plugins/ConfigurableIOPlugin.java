package de.unistuttgart.ims.coref.annotator.plugins;

import java.util.function.Consumer;

import javax.swing.JFrame;

public interface ConfigurableIOPlugin extends IOPlugin {
	void showInputConfigurationDialog(JFrame parent, Consumer<ConfigurableIOPlugin> callback);

	void showOutputConfigurationDialog();
}
