package de.unistuttgart.ims.coref.annotator;

import java.util.Iterator;

import javax.swing.JTextPane;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.v1.CommentAnchor;
import de.unistuttgart.ims.coref.annotator.api.v1.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModelListener;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.Event;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;

public abstract class AbstractTextWindow extends AbstractWindow implements HasTextView, CoreferenceModelListener {

	private static final long serialVersionUID = 1L;

	DocumentModel documentModel;
	HighlightManager highlightManager;
	JTextPane textPane;

	@Override
	public String getText() {
		return getJCas().getDocumentText();
	}

	@Override
	public JCas getJCas() {
		return documentModel.getJcas();
	}

	@Override
	public void entityEvent(FeatureStructureEvent event) {
		Event.Type eventType = event.getType();
		switch (eventType) {
		case Add:
			entityEventAdd(event);
			break;
		case Remove:
			entityEventRemove(event);
			break;
		case Update:
			entityEventUpdate(event);
			break;
		case Move:
			entityEventMove(event);
			break;
		case Merge:
			entityEventMerge(event);
			break;
		case Op:
			entityEventOp(event);
			break;
		case Init:
			entityEventInit(event);
		default:
		}
	}

	protected void entityEventAdd(FeatureStructureEvent event) {
		Iterator<FeatureStructure> iter = event.iterator(1);
		while (iter.hasNext()) {
			FeatureStructure fs = iter.next();
			if (fs instanceof Mention || fs instanceof DetachedMentionPart) {
				highlightManager.underline((Annotation) fs);
			} else if (fs instanceof CommentAnchor) {
				highlightManager.highlight((Annotation) fs);
			}
		}
	}

	protected void entityEventRemove(FeatureStructureEvent event) {
		Iterator<FeatureStructure> iter = event.iterator(1);
		while (iter.hasNext()) {
			FeatureStructure fs = iter.next();
			if (fs instanceof Mention) {
				if (((Mention) fs).getDiscontinuous() != null)
					highlightManager.unUnderline(((Mention) fs).getDiscontinuous());
				highlightManager.unUnderline((Annotation) fs);
			} else if (fs instanceof Annotation)
				highlightManager.unUnderline((Annotation) fs);

		}
	}

	protected void entityEventUpdate(FeatureStructureEvent event) {
		for (FeatureStructure fs : event) {
			if (fs instanceof Mention) {
				if (Util.isX(((Mention) fs).getEntity(), Constants.ENTITY_FLAG_HIDDEN))
					highlightManager.unUnderline((Annotation) fs);
				else
					highlightManager.underline((Annotation) fs);
			}
		}
	}

	protected void entityEventMove(FeatureStructureEvent event) {
	}

	protected void entityEventMerge(FeatureStructureEvent event) {

	}

	protected void entityEventOp(FeatureStructureEvent event) {

	}

	protected void entityEventInit(FeatureStructureEvent event) {
		CoreferenceModel cm = (CoreferenceModel) event.getSource();
		for (Mention m : cm.getMentions()) {
			highlightManager.underline(m);
			if (m.getDiscontinuous() != null)
				highlightManager.underline(m.getDiscontinuous());
		}
	}

	public JTextPane getTextPane() {
		return textPane;
	}

	public DocumentModel getDocumentModel() {
		return documentModel;
	}
}
