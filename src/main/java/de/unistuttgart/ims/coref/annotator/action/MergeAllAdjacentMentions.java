package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class MergeAllAdjacentMentions extends TargetedIkonAction<DocumentWindow> {

	private static final long serialVersionUID = 1L;

	public MergeAllAdjacentMentions(DocumentWindow dw) {
		super(dw, Strings.ACTION_MERGE_ADJACENT_MENTIONS, MaterialDesign.MDI_CALL_MERGE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JCas jcas = getTarget().getJCas();
		FSIterator<Mention> mentionIterator = jcas.getAnnotationIndex(Mention.class).iterator();

		Mention current = null, previous = null;
		while (mentionIterator.hasNext()) {
			previous = current;
			current = mentionIterator.next();
			if (previous == null)
				continue;
		}
	}

}
