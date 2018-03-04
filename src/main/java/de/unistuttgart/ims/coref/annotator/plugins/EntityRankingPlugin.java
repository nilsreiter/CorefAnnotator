package de.unistuttgart.ims.coref.annotator.plugins;

import org.apache.uima.jcas.JCas;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;

import de.unistuttgart.ims.coref.annotator.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.PotentialAnnotation;
import de.unistuttgart.ims.coref.annotator.api.Entity;

public interface EntityRankingPlugin extends Plugin {
	MutableSortedSet<Entity> rank(PotentialAnnotation potAnnotation, CoreferenceModel cModel, JCas jcas);
}
