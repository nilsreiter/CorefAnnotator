package de.unistuttgart.ims.coref.annotator.plugin.json;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.FlowControllerFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.unistuttgart.ims.coref.annotator.ExtensionFilters;
import de.unistuttgart.ims.coref.annotator.plugins.AbstractExportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.UimaExportPlugin;
import javafx.stage.FileChooser.ExtensionFilter;

public class Plugin extends AbstractExportPlugin implements UimaExportPlugin {

	@Override
	public String getDescription() {
		return "Export into token-based JSON, to be imported into rCat";
	}

	@Override
	public String getName() {
		return "rCat/JSON";
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(Constants.FLOW_KEY_TOKENIZER, AnalysisEngineFactory.createEngineDescription(BreakIteratorSegmenter.class,
				BreakIteratorSegmenter.PARAM_WRITE_SENTENCE, false));
		b.setFlowControllerDescription(FlowControllerFactory.createFlowControllerDescription(FlowController.class));
		return b.createAggregateDescription();
	}

	@Override
	public AnalysisEngineDescription getWriter(File f) throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(JSONWriter.class, JSONWriter.PARAM_FILE,
				f.getAbsolutePath()));
		return b.createAggregateDescription();
	}

	@Override
	public FileFilter getFileFilter() {
		return new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".json");
			}

			@Override
			public String getDescription() {
				return "JSON";
			}

		};
	}

	@Override
	public String getSuffix() {
		return ".json";
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return ExtensionFilters.json;
	}

}
