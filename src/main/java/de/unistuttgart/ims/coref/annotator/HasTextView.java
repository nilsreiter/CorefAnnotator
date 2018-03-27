package de.unistuttgart.ims.coref.annotator;

import org.apache.uima.jcas.JCas;

public interface HasTextView {
	String getText();

	Span getSelection();

	JCas getJCas();
}
