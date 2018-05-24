package de.unistuttgart.ims.coref.annotator.inspector.detector;

import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.Op;
import de.unistuttgart.ims.coref.annotator.inspector.DefaultIssueN;
import de.unistuttgart.ims.coref.annotator.inspector.Detector;
import de.unistuttgart.ims.coref.annotator.inspector.Issue;
import de.unistuttgart.ims.coref.annotator.inspector.IssueType;

public class MentionOfZeroLength implements Detector<Mention> {

	@Override
	public boolean detect(Mention object, char[] text) {
		return object.getBegin() == object.getEnd();
	}

	@Override
	public Issue getIssue(DocumentModel dm, Mention object) {
		DefaultIssueN<Mention> di = new DefaultIssueN<Mention>(dm, IssueType.MISTAKE, object,
				"Mention with zero length");
		di.addSolution("Remove mention", "Remove mention", iss -> {
			dm.getCoreferenceModel().edit(new Op.RemoveMention(object));
			return true;
		});
		di.addSolution("Increase extent", "Increase extent of mention", iss -> {
			return false;
		});
		return di;
	}

}
