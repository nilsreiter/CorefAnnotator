package de.unistuttgart.ims.coref.annotator;

import org.apache.uima.jcas.tcas.Annotation;

public interface CoreferenceModelListener {
	public void mentionAdded(Annotation m);

	public void annotationChanged(Annotation m);

	public void annotationSelected(Annotation m);

	public void annotationRemoved(Annotation m);

}
