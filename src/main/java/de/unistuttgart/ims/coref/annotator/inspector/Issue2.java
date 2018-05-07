package de.unistuttgart.ims.coref.annotator.inspector;

import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.uimautil.AnnotationUtil;

public class Issue2 extends Issue {

	Mention m;

	public Issue2(Mention mention) {
		this.m = mention;
		setDescription("Misplaced end boundary of mention");
	}

	@Override
	public IssueType getIssueType() {
		return IssueType.MISPLACED_END;
	}

	@Override
	public void solve() {
		AnnotationUtil.trimEnd(m, Checker.whitespace);
	}

}
