package de.unistuttgart.ims.coref.annotator.inspector.detector;

import java.util.function.Function;

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

public class BeginBoundary implements Detector<Mention> {

	@Override
	public boolean detect(Mention object, char[] text) {
		int b = object.getBegin();
		return ArrayUtils.contains(Detector.whitespace, text[b - 1])
				&& ArrayUtils.contains(Detector.whitespace, text[b]);
	}

	@Override
	public Issue getIssue(DocumentModel dm, Mention m) {
		Function<DefaultIssue<Mention>, Boolean> solver = iss -> {
			AnnotationUtil.trimBegin(iss.getInstance(), Checker.whitespace);
			dm.getCoreferenceModel().fireEvent(new FeatureStructureEvent(Event.Type.Update, iss.getInstance()));
			return true;
		};

		return new DefaultIssue<Mention>(dm, IssueType.MISTAKE, m, "Misplaced begin boundary of mention", solver);
	}

}
