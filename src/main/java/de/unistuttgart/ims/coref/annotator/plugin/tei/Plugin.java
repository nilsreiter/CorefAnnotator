package de.unistuttgart.ims.coref.annotator.plugin.tei;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ResourceInitializationException;

import de.unistuttgart.ims.coref.annotator.ExtensionFilters;
import de.unistuttgart.ims.coref.annotator.FileFilters;
import de.unistuttgart.ims.coref.annotator.plugins.ConfigurableIOPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.StylePlugin;
import de.unistuttgart.ims.coref.annotator.uima.EnsureMeta;
import javafx.stage.FileChooser.ExtensionFilter;

public class Plugin implements ConfigurableIOPlugin {

	boolean importIncludeHeader = true;

	@Override
	public String getDescription() {
		try {
			return IOUtils.toString(getClass().getResourceAsStream("/plugin.tei/description.txt"), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public String getName() {
		return "TEI/XML";
	}

	@Override
	public AnalysisEngineDescription getImporter() throws ResourceInitializationException {
		AggregateBuilder b = new AggregateBuilder();
		b.add(AnalysisEngineFactory.createEngineDescription(EnsureMeta.class));
		return b.createAggregateDescription();
	}

	@Override
	public AnalysisEngineDescription getExporter() throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(MapCorefToXmlElements.class);
	}

	@Override
	public CollectionReaderDescription getReader(File f) throws ResourceInitializationException {
		return CollectionReaderFactory.createReaderDescription(TeiReader.class, TeiReader.PARAM_SOURCE_LOCATION,
				f.getAbsoluteFile(), TeiReader.PARAM_TEXT_ROOT_SELECTOR, (importIncludeHeader ? "" : "TEI > text"),
				TeiReader.PARAM_LANGUAGE, "de", TeiReader.PARAM_DOCUMENT_ID, f.getName());
	}

	@Override
	public AnalysisEngineDescription getWriter(File f) throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(TeiWriter.class, TeiWriter.PARAM_OUTPUT_FILE,
				f.getAbsolutePath());
	}

	@Override
	public Class<? extends StylePlugin> getStylePlugin() {
		return TeiStylePlugin.class;
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
	public String[] getSupportedLanguages() {
		return null;
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return ExtensionFilters.tei;
	}

	@Override
	public void showInputConfigurationDialog(JFrame parent, Consumer<ConfigurableIOPlugin> callback) {
		JCheckBox teiHeaderCheckBox = new JCheckBox();
		teiHeaderCheckBox.setSelected(true);

		JDialog dialog = new JDialog(parent);

		dialog.getContentPane().setLayout(new GridLayout(0, 2));
		dialog.getContentPane().add(new JLabel("Include TEI header?"));
		dialog.getContentPane().add(teiHeaderCheckBox);

		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.err.println("!");
				importIncludeHeader = teiHeaderCheckBox.isSelected();
				dialog.dispose();
				callback.accept(Plugin.this);
			}
		});
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	@Override
	public void showOutputConfigurationDialog() {

	}

}