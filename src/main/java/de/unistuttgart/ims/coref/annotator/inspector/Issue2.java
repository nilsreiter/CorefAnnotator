package de.unistuttgart.ims.coref.annotator.inspector;

import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.Event;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;
import de.unistuttgart.ims.coref.annotator.inspector.Issue.InstanceIssue;
import de.unistuttgart.ims.uimautil.AnnotationUtil;

public class Issue2 extends InstanceIssue<Mention> {

	public Issue2(DocumentModel documentModel, Mention mention) {
		super(documentModel);
		setInstance(mention);
		setDescription("Misplaced end boundary of mention");
	}

	@Override
	public IssueType getIssueType() {
		return IssueType.MISPLACED_END;
	}

	@Override
	public boolean solve() {
		AnnotationUtil.trimEnd(getInstance(), Checker.whitespace);
		getDocumentModel().getCoreferenceModel().fireEvent(new FeatureStructureEvent(Event.Type.Update, getInstance()));
		return true;
	}

}
