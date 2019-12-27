package de.unistuttgart.ims.coref.annotator.plugin.b06.b03;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.ExtensionFilters;
import de.unistuttgart.ims.coref.annotator.FileFilters;
import de.unistuttgart.ims.coref.annotator.plugins.AbstractImportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.UimaImportPlugin;
import de.unistuttgart.ims.coref.annotator.uima.EnsureMeta;
import javafx.stage.FileChooser.ExtensionFilter;

public class ImportPlugin extends AbstractImportPlugin implements UimaImportPlugin {

	@Override
	public String getDescription() {
		try {
			return IOUtils.toString(getClass().getResourceAsStream(Constants.DESCRIPTION), Constants.UTF8);
		} catch (Exception e) {
			Annotator.logger.catching(e);
		}
		return StringUtils.EMPTY;
	}

	@Override
	public String getName() {
		return Constants.NAME;
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(EnsureMeta.class));
		return b.createAggregateDescription();
	}

	@Override
	public CollectionReaderDescription getReader(File f) throws ResourceInitializationException {
		return CollectionReaderFactory.createReaderDescription(TeiReader.class, TeiReader.PARAM_SOURCE_LOCATION,
				f.getAbsoluteFile(), TeiReader.PARAM_LANGUAGE, Constants.DE, TeiReader.PARAM_DOCUMENT_ID, f.getName());
	}

	@Override
	public FileFilter getFileFilter() {
		return FileFilters.tei;
	}

	@Override
	public String getSuffix() {
		return Constants.XML;
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return ExtensionFilters.tei;
	}

}
