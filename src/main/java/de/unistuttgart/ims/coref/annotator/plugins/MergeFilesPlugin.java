package de.unistuttgart.ims.coref.annotator.plugins;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.eclipse.collections.api.list.ImmutableList;

public class MergeFilesPlugin extends AbstractXmiPlugin implements IOPlugin {

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
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
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
	public String getSuffix() {
		return null;
	}

	@Override
	public String[] getSupportedLanguages() {
		return null;
	}

	public ImmutableList<File> getFiles() {
		return files;
	}

	public void setFiles(ImmutableList<File> files) {
		this.files = files;
	}

}
