package de.unistuttgart.ims.coref.annotator.inspector.detector;

import org.apache.commons.lang3.ArrayUtils;

import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.Event;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;
import de.unistuttgart.ims.coref.annotator.inspector.Checker;
import de.unistuttgart.ims.coref.annotator.inspector.DefaultIssue;
import de.unistuttgart.ims.coref.annotator.inspector.Detector;
import de.unistuttgart.ims.coref.annotator.inspector.Issue;
import de.unistuttgart.ims.coref.annotator.inspector.IssueType;
import de.unistuttgart.ims.uimautil.AnnotationUtil;

public class EndBoundary implements Detector<Mention> {

	@Override
	public boolean detect(Mention object, char[] text) {
		int e = object.getEnd();
		return ArrayUtils.contains(Detector.whitespace, text[e - 1])
				&& ArrayUtils.contains(Detector.whitespace, text[e]);
	}

	@Override
	public Issue getIssue(DocumentModel dm, Mention m) {
		return new DefaultIssue<Mention>(dm, IssueType.MISTAKE, m, "Misplaced end boundary of mention", iss -> {
			AnnotationUtil.trimEnd(iss.getInstance(), Checker.whitespace);
			dm.getCoreferenceModel().fireEvent(new FeatureStructureEvent(Event.Type.Update, iss.getInstance()));
			return true;
		});
	}

}
