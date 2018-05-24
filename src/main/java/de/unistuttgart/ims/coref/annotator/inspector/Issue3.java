package de.unistuttgart.ims.coref.annotator.inspector;

import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
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
		return IssueType.QUESTIONABLE;
	}

	@Override
	public boolean solve(int solution) {
		return false;
	}

	@Override
	public int getNumberOfSolutions() {
		return 1;
	}

	@Override
	public String getSolutionDescription(int solution) {
		return "this should be a longer description";
	}

	@Override
	public String getSolutionName(int solution) {
		return "solve";
	}

}
