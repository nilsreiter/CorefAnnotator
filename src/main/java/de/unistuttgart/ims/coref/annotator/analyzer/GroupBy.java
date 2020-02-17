package de.unistuttgart.ims.coref.annotator.analyzer;

import java.util.function.Function;

import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

enum GroupBy {
	COVEREDTEXT, ENTITY;

	public Function<Annotation, String> getFunction() {
		switch (this) {
		case ENTITY:
			return a -> ((Mention) a).getEntity().getLabel();
		default:
			return a -> a.getCoveredText();
		}
	}
}