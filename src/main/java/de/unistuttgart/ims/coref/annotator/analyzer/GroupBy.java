package de.unistuttgart.ims.coref.annotator.analyzer;

import java.util.function.Function;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.tcas.Annotation;

import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.uima.UimaUtil;

enum GroupBy {
	COVEREDTEXT, ENTITY;

	public Function<FeatureStructure, String> getFunction() {
		switch (this) {
		case ENTITY:
			return a -> ((Mention) a).getEntity().getLabel();
		default:
			return a -> {
				if (a instanceof Mention)
					return UimaUtil.getCoveredText((Mention) a);
				if (a instanceof Annotation)
					return ((Annotation) a).getCoveredText();
				return null;
			};
		}
	}
}