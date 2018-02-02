package de.unistuttgart.ims.coref.annotator;

import org.apache.uima.jcas.JCas;

public interface LoadingListener {
	public void jcasLoaded(JCas jcas);

	public void modelCreated(CoreferenceModel model, DocumentWindow documentWindow);
}
