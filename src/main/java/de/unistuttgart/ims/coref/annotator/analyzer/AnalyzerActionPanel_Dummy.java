package de.unistuttgart.ims.coref.annotator.analyzer;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public class AnalyzerActionPanel_Dummy extends AnalyzerActionPanel {

	private static final long serialVersionUID = 1L;

	public AnalyzerActionPanel_Dummy(DocumentModel documentModel, Iterable<Entity> entity) {
		super(documentModel, entity);
	}

	@Override
	public ACTION getType() {
		return null;
	}

	@Override
	public void setEntities(Iterable<Entity> entities) {

	}

}
