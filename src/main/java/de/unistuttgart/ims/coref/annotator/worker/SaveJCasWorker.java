package de.unistuttgart.ims.coref.annotator.worker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.function.BiConsumer;

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

		try (OutputStream os = new FileOutputStream(file)) {
			XmiCasSerializer.serialize(jcas.getCas(), os);
		}

		return null;
	}

	@Override
	protected void done() {
		consumer.accept(file, jcas);
	}

	public static BiConsumer<File, JCas> getConsumer(DocumentWindow target) {
		return (file, jcas) -> {
			Annotator.app.recentFiles.add(0, file);
			Annotator.app.refreshRecents();
			Annotator.app.setCurrentDirectory(file.getParentFile());
			target.setFile(file);
			target.setWindowTitle();
			target.stopIndeterminateProgress();
		};
	}
}
