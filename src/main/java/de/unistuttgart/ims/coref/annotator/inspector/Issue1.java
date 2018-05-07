package de.unistuttgart.ims.coref.annotator.inspector;

import de.unistuttgart.ims.coref.annotator.api.Mention;
import de.unistuttgart.ims.uimautil.AnnotationUtil;

public class Issue1 extends Issue {

	Mention m;

	public Issue1(Mention mention) {
		this.m = mention;
		setDescription("Misplaced begin boundary of mention");
	}

	@Override
	public IssueType getIssueType() {
		return IssueType.MISPLACED_BEGIN;
	}

	@Override
	public void solve() {
		AnnotationUtil.trimBegin(m, Checker.whitespace);
	}

}
