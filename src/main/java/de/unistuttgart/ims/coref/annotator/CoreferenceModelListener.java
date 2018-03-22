package de.unistuttgart.ims.coref.annotator;

import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;
import de.unistuttgart.ims.coref.annotator.document.AnnotationEvent;
import de.unistuttgart.ims.coref.annotator.document.FeatureStructureEvent;

public interface CoreferenceModelListener {

	@Deprecated
	public enum EventType {
		Add, Remove, Update
	};

	@Deprecated
	void annotationEvent(EventType eventType, Annotation annotation);

	void annotationEvent(AnnotationEvent event);

	@Deprecated
	void annotationMovedEvent(Annotation annotation, Object from, Object to);

	void entityEvent(FeatureStructureEvent event);

	@Deprecated
	void entityEvent(EventType eventType, Entity entity);

	@Deprecated
	void entityGroupEvent(EventType eventType, EntityGroup entity);

}
