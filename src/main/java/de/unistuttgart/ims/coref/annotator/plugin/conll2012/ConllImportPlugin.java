package de.unistuttgart.ims.coref.annotator.plugin.conll2012;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.plugin.dkpro.ImportDKpro;
import de.unistuttgart.ims.coref.annotator.plugins.AbstractImportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.UimaImportPlugin;
import de.unistuttgart.ims.coref.annotator.uima.CoNLL2012Reader;
import de.unistuttgart.ims.coref.annotator.uima.EnsureMeta;
import javafx.stage.FileChooser.ExtensionFilter;

public class ConllImportPlugin extends AbstractImportPlugin implements UimaImportPlugin {

	@Override
	public String getDescription() {
		return Constants.DESCRIPTION;
	}

	@Override
	public String getName() {
		return Constants.NAME;
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(ImportDKpro.class));
		b.add(AnalysisEngineFactory.createEngineDescription(EnsureMeta.class));
		return b.createAggregateDescription();
	}

	@Override
	public CollectionReaderDescription getReader(File f) throws ResourceInitializationException {
		return CollectionReaderFactory.createReaderDescription(CoNLL2012Reader.class,
				CoNLL2012Reader.PARAM_SOURCE_LOCATION, f.getAbsolutePath());
	}

	@Override
	public FileFilter getFileFilter() {
		return new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".conll");
			}

			@Override
			public String getDescription() {
				return "CoNLL 2012";
			}

		};
	}

	@Override
	public String getSuffix() {
		return ".conll";
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return new ExtensionFilter("CoNLL 2012", "*.conll");
	}

}
