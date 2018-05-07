package de.unistuttgart.ims.coref.annotator.inspector;

import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.inspector.Issue.InstanceIssue;

public class Issue3 extends InstanceIssue<Mention> {

	public Issue3(DocumentModel documentModel, Mention m) {
		super(documentModel);
		setInstance(m);
		setDescription("Begin does not fall on a token boundary.");
	}

	@Override
	public IssueType getIssueType() {
		return IssueType.DUMMY;
	}

	@Override
	public boolean solve() {
		return false;
	}

}
