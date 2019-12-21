package de.unistuttgart.ims.coref.annotator.plugins;

import java.util.function.Consumer;

import javax.swing.JFrame;

import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public interface ConfigurableExportPlugin {
	void showExportConfigurationDialog(JFrame parent, DocumentModel documentModel,
			Consumer<ConfigurableExportPlugin> callback);

}
