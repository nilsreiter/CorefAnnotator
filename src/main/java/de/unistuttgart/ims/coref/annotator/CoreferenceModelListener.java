package de.unistuttgart.ims.coref.annotator;

import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.Mention;

public interface CoreferenceModelListener {
	public void mentionAdded(Mention m);

	public void mentionChanged(Mention m);

	public void annotationSelected(Annotation m);

	void mentionRemoved(Mention m);

}
