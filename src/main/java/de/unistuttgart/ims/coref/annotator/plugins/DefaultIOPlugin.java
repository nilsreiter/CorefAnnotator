package de.unistuttgart.ims.coref.annotator.plugins;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileFilter;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.FlowControllerFactory;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.io.Files;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.unistuttgart.ims.coref.annotator.FileFilters;
import de.unistuttgart.ims.coref.annotator.TypeSystemVersion;
import de.unistuttgart.ims.coref.annotator.uima.converter.LEGACY_To_V1_0;

public final class DefaultIOPlugin extends AbstractXmiPlugin {

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "Default";
	}

	@Override
	public CollectionReaderDescription getReader(File f) throws ResourceInitializationException {
		try {
			Files.copy(f, new File(f.getAbsolutePath() + ".bak"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_LENIENT, true,
				XmiReader.PARAM_ADD_DOCUMENT_METADATA, false, XmiReader.PARAM_SOURCE_LOCATION, f.getAbsolutePath());
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(TypeSystemVersion.v1.name(), AnalysisEngineFactory.createEngineDescription(LEGACY_To_V1_0.class));
		b.setFlowControllerDescription(
				FlowControllerFactory.createFlowControllerDescription(ConvertFlowController.class));

		return b.createAggregateDescription();
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(NoOpAnnotator.class);
	}

	@Override
	public Class<? extends StylePlugin> getStylePlugin() {
		return null;
	}

	@Override
	public FileFilter getFileFilter() {
		return FileFilters.xmi;
	}

	@Override
	public String[] getSupportedLanguages() {
		return de.unistuttgart.ims.coref.annotator.Constants.SUPPORTED_LANGUAGES;
	}

}
