package de.unistuttgart.ims.coref.annotator.plugin.plaintext;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.unistuttgart.ims.coref.annotator.FileFilters;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;

public class Plugin implements IOPlugin {

	@Override
	public String getDescription() {
		return "Plain text";
	}

	@Override
	public String getName() {
		return "Plain text";
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(NoOpAnnotator.class);
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		return null;
	}

	@Override
	public CollectionReaderDescription getReader(File f) throws ResourceInitializationException {
		return CollectionReaderFactory.createReaderDescription(TextReader.class, TextReader.PARAM_SOURCE_LOCATION,
				f.getAbsolutePath(), TextReader.ENCODING_AUTO, true);
	}

	@Override
	public Class<? extends StylePlugin> getStylePlugin() {
		return null;
	}

	@Override
	public FileFilter getFileFilter() {
		return FileFilters.txt;
	}

	@Override
	public AnalysisEngineDescription getWriter(File f) throws ResourceInitializationException {
		return null;
	}

	@Override
	public String getSuffix() {
		return ".txt";
	}

	@Override
	public String[] getSupportedLanguages() {
		return de.unistuttgart.ims.coref.annotator.Constants.SUPPORTED_LANGUAGES;
	}
}
