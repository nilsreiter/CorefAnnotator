package de.unistuttgart.ims.coref.annotator.plugin.conll2012;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.FlowControllerFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.io.conll.Conll2012Writer;
import org.dkpro.core.tokit.BreakIteratorSegmenter;

import de.unistuttgart.ims.coref.annotator.plugins.AbstractExportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.UimaExportPlugin;
import de.unistuttgart.ims.coref.annotator.uima.SetDocumentId;
import javafx.stage.FileChooser.ExtensionFilter;

public class ConllExportPlugin extends AbstractExportPlugin implements UimaExportPlugin {

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
	public ExtensionFilter getExtensionFilter() {
		return new ExtensionFilter("CoNLL 2012", "*.conll");
	}

	@Override
	public String getDescription() {
		return Constants.DESCRIPTION;
	}

	@Override
	public String getName() {
		return Constants.NAME;
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
}
