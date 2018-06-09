package de.unistuttgart.ims.coref.annotator;

import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.Event;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;

public abstract class AbstractTextWindow extends AbstractWindow implements HasTextView, CoreferenceModelListener {

	private static final long serialVersionUID = 1L;

	DocumentModel documentModel;

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
		default:
		}
	}

	protected abstract void entityEventAdd(FeatureStructureEvent event);

	protected abstract void entityEventRemove(FeatureStructureEvent event);

	protected abstract void entityEventUpdate(FeatureStructureEvent event);

	protected abstract void entityEventMove(FeatureStructureEvent event);

	protected abstract void entityEventMerge(FeatureStructureEvent event);

	protected abstract void entityEventOp(FeatureStructureEvent event);
}
