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
import de.unistuttgart.ims.coref.annotator.plugins.AbstractIOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.UimaIOPlugin;
import de.unistuttgart.ims.coref.annotator.uima.EnsureMeta;
import javafx.stage.FileChooser.ExtensionFilter;

public class Plugin extends AbstractIOPlugin implements UimaIOPlugin {

	private static final String NAME = "B06: TEI for B03";
	private static final String DE = "de";
	private static final String XML = ".xml";
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
	public String getName() {
		return NAME;
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(EnsureMeta.class));
		return b.createAggregateDescription();
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(MapCorefToXmlElements.class);
	}

	@Override
	public CollectionReaderDescription getReader(File f) throws ResourceInitializationException {
		return CollectionReaderFactory.createReaderDescription(TeiReader.class, TeiReader.PARAM_SOURCE_LOCATION,
				f.getAbsoluteFile(), TeiReader.PARAM_LANGUAGE, DE, TeiReader.PARAM_DOCUMENT_ID, f.getName());
	}

	@Override
	public AnalysisEngineDescription getWriter(File f) throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(TeiWriter.class, TeiWriter.PARAM_OUTPUT_FILE,
				f.getAbsolutePath());
	}

	@Override
	public FileFilter getFileFilter() {
		return FileFilters.tei;
	}

	@Override
	public String getSuffix() {
		return XML;
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return ExtensionFilters.tei;
	}

}
