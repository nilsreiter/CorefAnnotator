package de.unistuttgart.ims.coref.annotator;

import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.Entity;

public interface CoreferenceModelListener {
	public void annotationAdded(Annotation m);

	public void annotationChanged(Annotation m);

	public void annotationRemoved(Annotation m);

	public void entityAdded(Entity entity);

	public void entityRemoved(Entity entity);
}
