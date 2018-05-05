package de.unistuttgart.ims.coref.annotator.uima;

import org.apache.uima.fit.component.JCasAnnotator_ImplBase;

import de.unistuttgart.ims.coref.annotator.FileFormat;

public abstract class TypeSystemVersionConverter extends JCasAnnotator_ImplBase {
	public abstract FileFormat getSource();

	public abstract FileFormat getTarget();
}
