package de.unistuttgart.ims.coref.annotator.worker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.function.BiConsumer;
import java.util.zip.GZIPOutputStream;

import javax.swing.SwingWorker;

import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;

public class SaveJCasWorker extends SwingWorker<Object, Object> {

	File file;
	JCas jcas;
	BiConsumer<File, JCas> consumer;

	public SaveJCasWorker(File file, JCas jcas, BiConsumer<File, JCas> func) {
		this.file = file;
		this.jcas = jcas;
		this.consumer = func;
	}

	@Override
	protected Object doInBackground() throws Exception {
		Annotator.logger.info("Saving ... ");
		OutputStream os = null;
		try {
			if (file.getName().endsWith(".ca2z")) {
				os = new GZIPOutputStream(new FileOutputStream(file));
			} else if (file.getName().endsWith(".ca2")) {
				os = new FileOutputStream(file);
			}

			if (os != null)
				XmiCasSerializer.serialize(jcas.getCas(), null, os, true, null);
		} finally {
			if (os != null)
				os.close();
		}

		return null;
	}

	@Override
	protected void done() {
		Annotator.logger.info("Saved.");
		consumer.accept(file, jcas);
	}

	public static BiConsumer<File, JCas> getConsumer(DocumentWindow target) {
		return (file, jcas) -> {
			Annotator.app.addRecentFile(file);
			Annotator.app.refreshRecents();
			Annotator.app.setCurrentDirectory(file.getParentFile());
			target.getDocumentModel().getHistory().clear();
			target.setFile(file);
			target.setWindowTitle();
			target.stopIndeterminateProgress();
		};
	}
}
