package de.unistuttgart.ims.coref.annotator.analyzer;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public class AnalyzerActionPanel_NeighbourRight extends AnalyzerActionPanel_Neighbour {

	private static final long serialVersionUID = 1L;

	public AnalyzerActionPanel_NeighbourRight(DocumentModel documentModel, Iterable<Entity> entity) {
		super(documentModel, entity);
		direction = DIRECTION.RIGHT;

		init();
		setEntities(entity);
	}

	@Override
	public ACTION getType() {
		return ACTION.NEIGHBOUR_RIGHT;
	}

}
