package de.unistuttgart.ims.coref.annotator.plugins;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.FlowControllerFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;
import de.unistuttgart.ims.coref.annotator.FileFilters;
import de.unistuttgart.ims.coref.annotator.TypeSystemVersion;
import de.unistuttgart.ims.coref.annotator.uima.converter.LEGACY_To_V1_0;
import de.unistuttgart.ims.uimautil.SetDocumentId;

public final class DefaultIOPlugin extends AbstractXmiPlugin {

	File lastFile;

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
		lastFile = f;
		return CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_LENIENT, true,
				XmiReader.PARAM_ADD_DOCUMENT_METADATA, false, XmiReader.PARAM_SOURCE_LOCATION, f.getAbsolutePath());
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();

		AggregateBuilder b1 = new AggregateBuilder();
		b1.add(AnalysisEngineFactory.createEngineDescription(SetDocumentId.class, SetDocumentId.PARAM_DOCUMENT_ID,
				lastFile.getName().replaceAll(getSuffix(), "") + ".bak"));
		b1.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,
				lastFile.getParentFile().getAbsolutePath(), XmiWriter.PARAM_USE_DOCUMENT_ID, true,
				XmiWriter.PARAM_OVERWRITE, true));
		b1.add(AnalysisEngineFactory.createEngineDescription(LEGACY_To_V1_0.class));
		b.add(TypeSystemVersion.v1.name(), b1.createAggregateDescription());

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
