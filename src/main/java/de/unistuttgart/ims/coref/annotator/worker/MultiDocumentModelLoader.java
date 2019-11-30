package de.unistuttgart.ims.coref.annotator.worker;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javax.swing.SwingWorker;

import org.apache.uima.jcas.JCas;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.document.MultiDocumentModel;

public class MultiDocumentModelLoader extends SwingWorker<MultiDocumentModel, Integer> {

	Consumer<MultiDocumentModel> consumer = null;
	JCas[] jcass;

	public MultiDocumentModelLoader(Consumer<MultiDocumentModel> consumer, JCas... jcas) {
		this.consumer = consumer;
		this.jcass = jcas;
	}

	@Override
	protected MultiDocumentModel doInBackground() throws Exception {
		MultiDocumentModel multiDocumentModel = new MultiDocumentModel();
		Lists.mutable.with(jcass).collect(jcas -> {
			DocumentModelLoader dml = new DocumentModelLoader(dm -> multiDocumentModel.addDocumentModel(dm), jcas);
			dml.execute();
			return dml;
		}).forEach(dml -> {
			try {
				dml.get();
			} catch (InterruptedException | ExecutionException e) {
				Annotator.logger.catching(e);
			}
		});
		multiDocumentModel.initialize();
		return multiDocumentModel;
	}

	@Override
	protected void done() {
		try {
			if (consumer != null)
				consumer.accept(get());
		} catch (InterruptedException | ExecutionException e) {
			Annotator.logger.catching(e);
		}
	}

}