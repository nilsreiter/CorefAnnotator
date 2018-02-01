package de.unistuttgart.ims.coref.annotator;

import org.apache.uima.fit.factory.AnnotationFactory;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class DiscourseEntityEntry {
	Entity jcasRepresentation;
	String label;

	public DiscourseEntityEntry(Entity entity, String label) {
		this.jcasRepresentation = entity;
		this.label = label;
	}

	public void registerDrop(PotentialAnnotation anno) {
		Mention m = AnnotationFactory.createAnnotation(anno.getTextView().getJCas(), anno.getBegin(), anno.getEnd(),
				Mention.class);
		m.setEntity(jcasRepresentation);
	}

	public Entity getJcasRepresentation() {
		return jcasRepresentation;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return getLabel();
	}
}
