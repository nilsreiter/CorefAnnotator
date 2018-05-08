package de.unistuttgart.ims.coref.annotator.plugin.conll2012;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.FlowControllerFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.io.conll.Conll2012Writer;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unistuttgart.ims.coref.annotator.plugin.dkpro.ImportDKpro;
import de.unistuttgart.ims.coref.annotator.plugins.IOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;
import de.unistuttgart.ims.coref.annotator.uima.CoNLL2012Reader;
import de.unistuttgart.ims.uimautil.SetDocumentId;

public class Plugin implements IOPlugin {

	@Override
	public String getDescription() {
		return "Import from and Export into CoNLL 2012 format. All columns except surface, sentence and coreference are empty.";
	}

	@Override
	public String getName() {
		return "CoNLL 2012";
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(ImportDKpro.class));
		return b.createAggregateDescription();
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(Cleaner.class));
		b.add(Constants.FLOW_KEY_TOKENIZER, AnalysisEngineFactory.createEngineDescription(BreakIteratorSegmenter.class,
				BreakIteratorSegmenter.PARAM_WRITE_SENTENCE, false));
		b.add(Constants.FLOW_KEY_SENTENCE_SPLITTER, AnalysisEngineFactory.createEngineDescription(
				BreakIteratorSegmenter.class, BreakIteratorSegmenter.PARAM_WRITE_TOKEN, false));
		b.add(Constants.FLOW_KEY_CONVERTER, AnalysisEngineFactory.createEngineDescription(Exporter.class));
		b.setFlowControllerDescription(FlowControllerFactory.createFlowControllerDescription(FlowController.class));
		return b.createAggregateDescription();
	}

	@Override
	public AnalysisEngineDescription getWriter(File f) throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(SetDocumentId.class, SetDocumentId.PARAM_DOCUMENT_ID,
				f.getName().replaceAll(getSuffix(), "")));
		b.add(AnalysisEngineFactory.createEngineDescription(Conll2012Writer.class,
				Conll2012Writer.PARAM_TARGET_LOCATION, f.getParentFile().getAbsolutePath(),
				Conll2012Writer.PARAM_USE_DOCUMENT_ID, true, Conll2012Writer.PARAM_OVERWRITE, true));
		return b.createAggregateDescription();
	}

	@Override
	public CollectionReaderDescription getReader(File f) throws ResourceInitializationException {
		return CollectionReaderFactory.createReaderDescription(CoNLL2012Reader.class,
				CoNLL2012Reader.PARAM_SOURCE_LOCATION, f.getAbsolutePath());
	}

	@Override
	public Class<? extends StylePlugin> getStylePlugin() {
		return null;
	}

	@Override
	public FileFilter getFileFilter() {
		return new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".conll");
			}

			@Override
			public String getDescription() {
				return "CoNLL 2012";
			}

		};
	}

	@Override
	public String getSuffix() {
		return ".conll";
	}

	@Override
	public String[] getSupportedLanguages() {
		return de.unistuttgart.ims.coref.annotator.Constants.SUPPORTED_LANGUAGES;
	}

}
