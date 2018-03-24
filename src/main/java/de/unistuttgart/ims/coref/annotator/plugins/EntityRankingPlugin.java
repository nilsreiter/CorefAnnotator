package de.unistuttgart.ims.coref.annotator.plugins;

import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;

import de.unistuttgart.ims.coref.annotator.Span;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;

public interface EntityRankingPlugin extends Plugin {
	MutableSortedSet<Entity> rank(Span potAnnotation, CoreferenceModel cModel, JCas jcas);
}
