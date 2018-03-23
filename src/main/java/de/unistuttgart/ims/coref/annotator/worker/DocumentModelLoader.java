package de.unistuttgart.ims.coref.annotator.worker;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

import javax.swing.SwingWorker;

import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CoreferenceModelListener;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.document.CommentsModel;
import de.unistuttgart.ims.coref.annotator.document.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.EntityTreeModel;

public class DocumentModelLoader extends SwingWorker<DocumentModel, Integer> {

	@Deprecated
	DocumentWindow documentWindow;
	Consumer<DocumentModel> consumer = null;
	CoreferenceModelListener coreferenceModelListener = null;
	JCas jcas;

	@Deprecated
	public DocumentModelLoader(DocumentWindow documentWindow, JCas jcas) {
		this.documentWindow = documentWindow;
		this.jcas = jcas;
	}

	public DocumentModelLoader(Consumer<DocumentModel> consumer, JCas jcas) {
		this.consumer = consumer;
		this.jcas = jcas;
	}

	protected DocumentModel load(Preferences preferences) {
		Annotator.logger.debug("Starting loading of coreference model");
		DocumentModel documentModel = new DocumentModel(jcas);

		CoreferenceModel cModel = new CoreferenceModel(documentModel, preferences);
		if (getCoreferenceModelListener() != null)
			cModel.addCoreferenceModelListener(getCoreferenceModelListener());
		cModel.initialPainting();

		EntityTreeModel etm = new EntityTreeModel(cModel);

		documentModel.setCoreferenceModel(cModel);
		documentModel.setTreeModel(etm);
		documentModel.setCommentsModel(new CommentsModel(documentModel));

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

	public CoreferenceModelListener getCoreferenceModelListener() {
		return coreferenceModelListener;
	}

	public void setCoreferenceModelListener(CoreferenceModelListener coreferenceModelListener) {
		this.coreferenceModelListener = coreferenceModelListener;
	}

}