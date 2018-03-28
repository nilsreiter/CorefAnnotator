package de.unistuttgart.ims.coref.annotator.plugin.tei;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.FileFilters;
import de.unistuttgart.ims.coref.annotator.plugin.quadrama.QDStylePlugin;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;

public class Plugin implements IOPlugin {

	@Override
	public String getDescription() {
		try {
			return IOUtils.toString(getClass().getResourceAsStream("/plugin.tei/description.txt"), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public String getName() {
		return "TEI/XML";
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(NoOpAnnotator.class));
		return b.createAggregateDescription();
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(MapCorefToXmlElements.class);
	}

	@Override
	public CollectionReaderDescription getReader(File f) throws ResourceInitializationException {
		return CollectionReaderFactory.createReaderDescription(TeiReader.class, TeiReader.PARAM_SOURCE_LOCATION,
				f.getAbsoluteFile(), TeiReader.PARAM_LANGUAGE, "de", TeiReader.PARAM_DOCUMENT_ID, f.getName());
	}

	@Override
	public AnalysisEngineDescription getWriter(File f) throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(TeiWriter.class, TeiWriter.PARAM_OUTPUT_FILE,
				f.getAbsolutePath());
	}

	@Override
	public Class<? extends StylePlugin> getStylePlugin() {
		return QDStylePlugin.class;
	}

	@Override
	public FileFilter getFileFilter() {
		return FileFilters.xml;
	}

	@Override
	public String getSuffix() {
		return ".xml";
	}

	@Override
	public String[] getSupportedLanguages() {
		return null;
	}

}
