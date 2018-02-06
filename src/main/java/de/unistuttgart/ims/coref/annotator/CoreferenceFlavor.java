package de.unistuttgart.ims.coref.annotator;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.plugins.io.dkpro.ImportDKpro;
import de.unistuttgart.ims.coref.annotator.plugins.io.quadrama.ImportQuaDramA;
import de.unistuttgart.ims.coref.annotator.uima.ImportCRETA;

public enum CoreferenceFlavor {
	Default, QuaDramA, DKpro, CRETA, AutoDetect;

	public AnalysisEngine getAnalysisEngine() throws ResourceInitializationException {
		switch (this) {
		case CRETA:
			return AnalysisEngineFactory.createEngine(ImportCRETA.class);
		case DKpro:
			return AnalysisEngineFactory.createEngine(ImportDKpro.class);
		case QuaDramA:
			return AnalysisEngineFactory.createEngine(ImportQuaDramA.class);
		default:
			return AnalysisEngineFactory.createEngine(NoOpAnnotator.class);
		}
	}
}
