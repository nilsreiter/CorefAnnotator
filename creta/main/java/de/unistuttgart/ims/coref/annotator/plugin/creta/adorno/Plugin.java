package de.unistuttgart.ims.coref.annotator.plugin.creta.adorno;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.unistuttgart.ims.coref.annotator.ExtensionFilters;
import de.unistuttgart.ims.coref.annotator.FileFilters;
import de.unistuttgart.ims.coref.annotator.plugins.AbstractImportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;
import de.unistuttgart.ims.coref.annotator.plugins.UimaImportPlugin;
import javafx.stage.FileChooser.ExtensionFilter;

public class Plugin extends AbstractImportPlugin implements UimaImportPlugin {

	@Override
	public String getDescription() {
		return "Importer for Adorno annotations done in CRETAnno";
	}

	@Override
	public String getName() {
		return "CRETA/Adorno";
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(Importer.class);
	}

	@Override
	public Class<? extends StylePlugin> getStylePlugin() {
		return null;
	}

	@Override
	public CollectionReaderDescription getReader(File f) throws ResourceInitializationException {
		return CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_LENIENT, true,
				XmiReader.PARAM_ADD_DOCUMENT_METADATA, false, XmiReader.PARAM_SOURCE_LOCATION, f.getAbsolutePath());
	}

	@Override
	public FileFilter getFileFilter() {
		return FileFilters.xmi;
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return ExtensionFilters.xmi;
	}

	@Override
	public String getSuffix() {
		return ".xmi";
	}

}
