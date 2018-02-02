package de.unistuttgart.ims.coref.annotator;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.uima.ImportQuaDramA;

public enum CoreferenceFlavor {
	Default, QuaDramA, DKpro, AutoDetect;

	public AnalysisEngine getAnalysisEngine() throws ResourceInitializationException {
		switch (this) {
		case QuaDramA:
			return AnalysisEngineFactory.createEngine(ImportQuaDramA.class);
		default:
			return AnalysisEngineFactory.createEngine(NoOpAnnotator.class);
		}
	}
}
