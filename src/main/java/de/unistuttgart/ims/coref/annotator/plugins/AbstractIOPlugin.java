package de.unistuttgart.ims.coref.annotator.plugins;

import java.io.File;
import java.util.function.Consumer;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import javafx.stage.FileChooser.ExtensionFilter;

public abstract class AbstractIOPlugin implements IOPlugin {
	private static final String DESCRIPTION = "/description.txt";
	private static final String UTF8 = "UTF-8";

	@Override
	public String getDescription() {
		try {
			return IOUtils.toString(getClass().getResourceAsStream(DESCRIPTION), UTF8);
		} catch (Exception e) {
			Annotator.logger.catching(e);
		}
		return StringUtils.EMPTY;
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		return null;
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		return null;
	}

	@Override
	public CollectionReaderDescription getReader(File f) throws ResourceInitializationException {
		return null;
	}

	@Override
	public AnalysisEngineDescription getWriter(File f) throws ResourceInitializationException {
		return null;
	}

	@Override
	public Class<? extends StylePlugin> getStylePlugin() {
		return null;
	}

	@Override
	public FileFilter getFileFilter() {
		return null;
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return null;
	}

	@Override
	public String getSuffix() {
		return null;
	}

	@Override
	public String[] getSupportedLanguages() {
		return de.unistuttgart.ims.coref.annotator.Constants.SUPPORTED_LANGUAGES;
	}

	@Override
	public Consumer<File> getPostExportAction() {
		return null;
	}

	@Override
	public Ikon getIkon() {
		return MaterialDesign.MDI_FILE_EXPORT;
	}

}
