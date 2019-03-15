package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Sets;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class SelectNextMentionAction extends TargetedIkonAction<DocumentWindow> {

	public SelectNextMentionAction(DocumentWindow dw) {
		super(dw, null);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		int low = getTarget().getTextPane().getSelectionStart();
		int high = getTarget().getTextPane().getSelectionEnd();
		MutableSet<? extends Annotation> annotations = Sets.mutable
				.withAll(getTarget().getDocumentModel().getCoreferenceModel().getMentions(low));
		MutableSet<Mention> mentions = annotations.selectInstancesOf(Mention.class)
				.select(a -> a.getBegin() == low && a.getEnd() == high);
		Mention nextMention = null;
		if (mentions.isEmpty()) {
			nextMention = getTarget().getDocumentModel().getCoreferenceModel().getNextMention(high);
		} else if (mentions.size() == 1) {
			Mention currentMention = mentions.getOnly();
			nextMention = JCasUtil.selectFollowing(Mention.class, currentMention, 1).get(0);
		}
		if (nextMention != null)
			getTarget().annotationSelected(nextMention);

	}

}