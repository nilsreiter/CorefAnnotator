package de.unistuttgart.ims.coref.annotator.uima;

import org.apache.uima.fit.component.JCasAnnotator_ImplBase;

import de.unistuttgart.ims.coref.annotator.TypeSystemVersion;

public abstract class TypeSystemVersionConverter extends JCasAnnotator_ImplBase {
	public abstract TypeSystemVersion getSource();

	public abstract TypeSystemVersion getTarget();
}
