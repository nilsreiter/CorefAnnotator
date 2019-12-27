package de.unistuttgart.ims.coref.annotator.plugins;

import java.io.OutputStream;

import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public interface DocumentModelExportPlugin extends ExportPlugin {
	void write(DocumentModel documentModel, OutputStream outputStream);
}
