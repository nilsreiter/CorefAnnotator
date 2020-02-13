package de.unistuttgart.ims.coref.annotator.uima;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.eclipse.collections.api.list.ImmutableList;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.unistuttgart.ims.coref.annotator.plugins.AbstractImportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;
import de.unistuttgart.ims.coref.annotator.plugins.UimaImportPlugin;
import javafx.stage.FileChooser.ExtensionFilter;

public class MergeFilesPlugin extends AbstractImportPlugin implements UimaImportPlugin {

	ImmutableList<File> files;

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getName() {
		return getClass().getName();
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		for (File f : files.subList(1, files.size())) {
			b.add(AnalysisEngineFactory.createEngineDescription(MergeAnnotations.class, MergeAnnotations.PARAM_INPUT,
					f.getAbsolutePath()));
		}
		return b.createAggregateDescription();
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
	public String getSuffix() {
		return null;
	}

	public ImmutableList<File> getFiles() {
		return files;
	}

	public void setFiles(ImmutableList<File> files) {
		this.files = files;
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return null;
	}

	@Override
	public CollectionReaderDescription getReader(File f) throws ResourceInitializationException {
		return CollectionReaderFactory.createReaderDescription(XmiReader.class, XmiReader.PARAM_LENIENT, true,
				XmiReader.PARAM_ADD_DOCUMENT_METADATA, false, XmiReader.PARAM_SOURCE_LOCATION, f.getAbsolutePath());
	}

}
