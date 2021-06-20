package de.unistuttgart.ims.coref.annotator.plugin.quadrama.tei;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.ExtensionFilters;
import de.unistuttgart.ims.coref.annotator.FileFilters;
import de.unistuttgart.ims.coref.annotator.plugins.AbstractExportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.UimaExportPlugin;
import javafx.stage.FileChooser.ExtensionFilter;

public class QuadramaTeiExportPlugin extends AbstractExportPlugin implements UimaExportPlugin {

	@Override
	public String getDescription() {
		try {
			return IOUtils.toString(getClass().getResourceAsStream("description.txt"), "UTF-8");
		} catch (Exception e) {
			// TODO: find out why this doesn't work
			// e.printStackTrace();
		}
		return "";
	}

	@Override
	public String getName() {
		return "QuaDramA/TEI";
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(MapCorefToXmlElements.class);
	}

	@Override
	public AnalysisEngineDescription getWriter(File f) throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(TeiWriter.class, TeiWriter.PARAM_OUTPUT_FILE,
				f.getAbsolutePath());
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
	public ExtensionFilter getExtensionFilter() {
		return ExtensionFilters.tei;
	}

}
