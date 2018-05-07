package de.unistuttgart.ims.coref.annotator.inspector;

public abstract class Issue {

	String description;

	public abstract IssueType getIssueType();

	public abstract void solve();

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
