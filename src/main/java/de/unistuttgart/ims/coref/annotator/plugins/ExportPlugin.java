package de.unistuttgart.ims.coref.annotator.plugins;

import java.io.File;
import java.util.function.Consumer;

public interface ExportPlugin extends IOPlugin {
	Consumer<File> getPostExportAction();

}
