package de.unistuttgart.ims.coref.annotator.plugins;

import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public interface DocumentModelExportPlugin extends ExportPlugin {
	void write(DocumentModel documentModel, Appendable outputStream);
}
