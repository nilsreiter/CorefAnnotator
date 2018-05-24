package de.unistuttgart.ims.coref.annotator.inspector;

import java.util.function.Function;

import org.apache.uima.jcas.cas.TOP;

import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.inspector.Issue.InstanceIssue;

public class DefaultIssue<T extends TOP> extends InstanceIssue<T> {

	IssueType issueType;
	Function<DefaultIssue<T>, Boolean> solver;
	String solutionDescription;

	public DefaultIssue(DocumentModel documentModel, IssueType issueType, T instance, String description,
			Function<DefaultIssue<T>, Boolean> solver) {
		super(documentModel);
		setInstance(instance);
		setDescription(description);
		this.issueType = issueType;
		this.solver = solver;
	}

	@Override
	public IssueType getIssueType() {
		return issueType;
	}

	@Override
	public boolean solve(int solution) {
		return solver.apply(this);
	}

	@Override
	public int getNumberOfSolutions() {
		return 1;
	}

	@Override
	public String getSolutionDescription(int solution) {
		return solutionDescription;
	}

	@Override
	public String getSolutionName(int solution) {
		return "solve";
	}

	public void setSolutionDescription(String desc) {
		solutionDescription = desc;
	}

}
