package de.unistuttgart.ims.coref.annotator.plugin.tei;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.ExtensionFilters;
import de.unistuttgart.ims.coref.annotator.FileFilters;
import de.unistuttgart.ims.coref.annotator.plugins.AbstractExportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.UimaExportPlugin;
import javafx.stage.FileChooser.ExtensionFilter;

public class TeiExportPlugin extends AbstractExportPlugin implements UimaExportPlugin {

	@Override
	public String getDescription() {
		try {
			return IOUtils.toString(getClass().getResourceAsStream("/plugins/tei/description.txt"), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public String getName() {
		return Constants.NAME;
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
		return FileFilters.tei;
	}

	@Override
	public String getSuffix() {
		return ".xml";
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return ExtensionFilters.tei;
	}

	@Override
	public Ikon getIkon() {
		return MaterialDesign.MDI_FILE_XML;
	}

}