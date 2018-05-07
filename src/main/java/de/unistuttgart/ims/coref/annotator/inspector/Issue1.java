package de.unistuttgart.ims.coref.annotator.inspector;

import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.Event;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;
import de.unistuttgart.ims.coref.annotator.inspector.Issue.InstanceIssue;
import de.unistuttgart.ims.uimautil.AnnotationUtil;

public class Issue1 extends InstanceIssue<Mention> {

	public Issue1(DocumentModel documentModel, Mention mention) {
		super(documentModel);
		setInstance(mention);
		setDescription("Misplaced begin boundary of mention");
	}

	@Override
	public IssueType getIssueType() {
		return IssueType.MISPLACED_BEGIN;
	}

	@Override
	public boolean solve() {
		AnnotationUtil.trimBegin(getInstance(), Checker.whitespace);
		getDocumentModel().getCoreferenceModel().fireEvent(new FeatureStructureEvent(Event.Type.Update, getInstance()));
		return true;
	}

}
