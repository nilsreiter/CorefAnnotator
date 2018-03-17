package de.unistuttgart.ims.coref.annotator.worker;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

import javax.swing.SwingWorker;

import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CoreferenceModelListener;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;

public class DocumentModelLoader extends SwingWorker<CoreferenceModel, Integer> {

	@Deprecated
	DocumentWindow documentWindow;
	Consumer<CoreferenceModel> consumer = null;
	CoreferenceModelListener coreferenceModelListener;
	JCas jcas;

	@Deprecated
	public DocumentModelLoader(DocumentWindow documentWindow, JCas jcas) {
		this.documentWindow = documentWindow;
		this.jcas = jcas;
	}

	public DocumentModelLoader(Consumer<CoreferenceModel> consumer, JCas jcas) {
		this.consumer = consumer;
		this.jcas = jcas;
	}

	protected CoreferenceModel load(Preferences preferences) {
		Annotator.logger.debug("Starting loading of coreference model");

		CoreferenceModel cModel;
		cModel = new CoreferenceModel(jcas, preferences);

		return cModel;
	}

	@Override
	protected CoreferenceModel doInBackground() throws Exception {
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

	public CoreferenceModelListener getCoreferenceModelListener() {
		return coreferenceModelListener;
	}

	public void setCoreferenceModelListener(CoreferenceModelListener coreferenceModelListener) {
		this.coreferenceModelListener = coreferenceModelListener;
	}

}