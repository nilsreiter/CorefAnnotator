package de.unistuttgart.ims.coref.annotator;

import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;

public interface CoreferenceModelListener {
	public enum Event {
		Add, Remove, Update
	};

	void annotationEvent(Event event, Annotation annotation);

	void annotationMovedEvent(Annotation annotation, Object from, Object to);

	void entityEvent(Event event, Entity entity);

	void entityGroupEvent(Event event, EntityGroup entity);

}
