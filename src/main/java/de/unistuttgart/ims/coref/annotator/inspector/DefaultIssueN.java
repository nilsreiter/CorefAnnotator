package de.unistuttgart.ims.coref.annotator.inspector;

import java.util.function.Function;

import org.apache.uima.jcas.cas.TOP;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.inspector.Issue.InstanceIssue;

public class DefaultIssueN<T extends TOP> extends InstanceIssue<T> {

	IssueType issueType;
	MutableList<String> solutionDescription = Lists.mutable.empty();
	MutableList<Function<DefaultIssueN<T>, Boolean>> solver = Lists.mutable.empty();
	MutableList<String> solutionName = Lists.mutable.empty();

	public DefaultIssueN(DocumentModel documentModel, IssueType issueType, T instance, String description) {
		super(documentModel);
		setInstance(instance);
		setDescription(description);
		this.issueType = issueType;
	}

	public void addSolution(String name, String description, Function<DefaultIssueN<T>, Boolean> solver) {
		this.solutionName.add(name);
		this.solutionDescription.add(description);
		this.solver.add(solver);
	}

	@Override
	public IssueType getIssueType() {
		return issueType;
	}

	@Override
	public boolean solve(int solution) {
		return solver.get(solution).apply(this);
	}

	@Override
	public int getNumberOfSolutions() {
		return solutionDescription.size();
	}

	@Override
	public String getSolutionDescription(int solution) {
		return solutionDescription.get(solution);
	}

	@Override
	public String getSolutionName(int solution) {
		return solutionName.get(solution);
	}

}
