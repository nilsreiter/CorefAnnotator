package de.unistuttgart.ims.coref.annotator.inspector;

import de.unistuttgart.ims.coref.annotator.Annotator;

public enum IssueType {
	DUMMY, MISTAKE, QUESTIONABLE;

	public String getToolTip() {
		switch (this) {
		case MISTAKE:
		case QUESTIONABLE:
			return Annotator.getString("inspector.issue.type." + this.name().toLowerCase());
		default:
			return null;
		}
	}
}
