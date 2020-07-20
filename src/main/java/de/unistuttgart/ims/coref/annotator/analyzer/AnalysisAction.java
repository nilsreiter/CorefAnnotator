package de.unistuttgart.ims.coref.annotator.analyzer;

import de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

enum AnalysisAction {
	MENTION, NEIGHBOUR, TEXTLOCATION, Flag, DUMMY;

	AnalyzerActionPanel getObject(DocumentModel documentModel, Iterable<Entity> entity) {
		switch (this) {
		case TEXTLOCATION:
			return new AnalyzerActionPanel_TextLocation(documentModel, entity);
		case NEIGHBOUR:
			return new AnalyzerActionPanel_Neighbour(documentModel, entity);
		case MENTION:
			return new AnalyzerActionPanel_Mention(documentModel, entity);
		case Flag:
			return new AnalyzerActionPanel_Flag(documentModel, entity);
		default:
			return new AnalyzerActionPanel_Dummy(documentModel, entity);
		}
	}
}