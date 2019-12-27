package de.unistuttgart.ims.coref.annotator.worker;

import java.io.File;
import java.util.function.BiConsumer;

import javax.swing.SwingWorker;

import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.CasCopier;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.plugins.DocumentModelExportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.ExportPlugin;
import de.unistuttgart.ims.coref.annotator.plugins.UimaExportPlugin;

public class ExportWorker extends SwingWorker<Object, Object> {

	File file;
	JCas jcas;
	BiConsumer<File, JCas> consumer;
	ExportPlugin plugin;

	public ExportWorker(File file, JCas jcas, ExportPlugin plugin, BiConsumer<File, JCas> consumer) {
		this.file = file;
		this.jcas = jcas;
		this.plugin = plugin;
		this.consumer = consumer;
	}

	@Override
	protected Object doInBackground() throws Exception {
		Annotator.logger.info("Exporting into file {} using plugin {}", file, plugin.getName());

		JCas newJCas = JCasFactory.createJCas();

		CasCopier.copyCas(jcas.getCas(), newJCas.getCas(), true, false);
		if (plugin instanceof UimaExportPlugin)
			SimplePipeline.runPipeline(newJCas, ((UimaExportPlugin) plugin).getExporter(),
					((UimaExportPlugin) plugin).getWriter(file));
		else if (plugin instanceof DocumentModelExportPlugin) {
			// TODO: implement
		}
		return null;
	}

	@Override
	protected void done() {
		consumer.accept(file, jcas);
		if (plugin.getPostExportAction() != null)
			plugin.getPostExportAction().accept(file);
	}

}
