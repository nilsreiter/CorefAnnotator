package de.unistuttgart.ims.coref.annotator.worker;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

import javax.swing.SwingWorker;

import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public class DocumentModelLoader extends SwingWorker<DocumentModel, Integer> {

	Consumer<DocumentModel> consumer = null;
	JCas jcas;

	public DocumentModelLoader(Consumer<DocumentModel> consumer, JCas jcas) {
		this.consumer = consumer;
		this.jcas = jcas;
	}

	protected DocumentModel load(Preferences preferences) {
		Annotator.logger.debug("Starting loading of coreference model");
		DocumentModel documentModel = new DocumentModel(jcas, preferences);

		documentModel.initialize();

		return documentModel;
	}

	@Override
	protected DocumentModel doInBackground() throws Exception {
		return load(Annotator.app.getPreferences());
	}

	@Override
	protected void done() {
		try {
			consumer.accept(get());
		} catch (InterruptedException | ExecutionException e) {
			Annotator.logger.catching(e);
		}
	}

}