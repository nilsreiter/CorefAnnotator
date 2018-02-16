package de.unistuttgart.ims.coref.annotator.plugin.conll2012;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.io.conll.Conll2012Writer;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;

public class Plugin implements IOPlugin {

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return "CoNLL 2012";
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		return null;
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(Exporter.class);
	}

	@Override
	public AnalysisEngineDescription getWriter(File f) throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(Conll2012Writer.class,
				Conll2012Writer.PARAM_TARGET_LOCATION, f.getAbsolutePath());
	}

	@Override
	public CollectionReaderDescription getReader(File f) throws ResourceInitializationException {
		return null;
	}

	@Override
	public Class<? extends StylePlugin> getStylePlugin() {
		return null;
	}

	@Override
	public FileFilter getFileFilter() {
		return new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".csv");
			}

			@Override
			public String getDescription() {
				return "CoNLL 2012";
			}

		};
	}

}
