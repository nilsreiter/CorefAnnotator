package de.unistuttgart.ims.coref.annotator.plugins;

import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.coref.annotator.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.PotentialAnnotation;

public interface EntityRankingPlugin extends Plugin {
	void rank(PotentialAnnotation potAnnotation, CoreferenceModel cModel, JCas jcas);
}
