package de.unistuttgart.ims.coref.annotator.plugin.dkpro;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.unistuttgart.ims.coref.annotator.ExtensionFilters;
import de.unistuttgart.ims.coref.annotator.FileFilters;
import de.unistuttgart.ims.coref.annotator.plugins.AbstractImportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.UimaImportPlugin;
import de.unistuttgart.ims.coref.annotator.uima.EnsureMeta;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * TODO: Compile without access to java classes
 *
 */
public class DKproPlugin extends AbstractImportPlugin implements UimaImportPlugin {

	@Override
	public String getDescription() {
		return "Imports coreference annotations in the DKpro type system (also builtin to WebAnno)";
	}

	@Override
	public String getName() {
		return "DKpro";
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(ImportDKpro.class));
		b.add(AnalysisEngineFactory.createEngineDescription(EnsureMeta.class));
		return b.createAggregateDescription();
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
	public CollectionReaderDescription getReader(File f) throws ResourceInitializationException {
		return CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_LENIENT, true,
				XmiReader.PARAM_ADD_DOCUMENT_METADATA, false, XmiReader.PARAM_SOURCE_LOCATION, f.getAbsolutePath());
	}

	@Override
	public String getSuffix() {
		return ".xmi";
	}

}
