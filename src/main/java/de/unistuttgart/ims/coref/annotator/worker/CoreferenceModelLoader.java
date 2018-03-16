package de.unistuttgart.ims.coref.annotator.worker;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

import javax.swing.SwingWorker;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.CoreferenceModelListener;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class CoreferenceModelLoader extends SwingWorker<CoreferenceModel, Integer> {

	private DocumentWindow documentWindow;
	Consumer<CoreferenceModel> consumer = null;
	JCas jcas;

	@Deprecated
	public CoreferenceModelLoader(DocumentWindow documentWindow, JCas jcas) {
		this.documentWindow = documentWindow;
		this.jcas = jcas;
	}

	public CoreferenceModelLoader(Consumer<CoreferenceModel> consumer, JCas jcas) {
		this.consumer = consumer;
		this.jcas = jcas;
	}

	protected CoreferenceModel load(CoreferenceModelListener listener, Preferences preferences) {
		Annotator.logger.debug("Starting loading of coreference model");

		CoreferenceModel cModel;
		cModel = new CoreferenceModel(jcas, preferences);
		cModel.addCoreferenceModelListener(listener);

		Lists.immutable.withAll(JCasUtil.select(jcas, Entity.class)).forEach(e -> {
			cModel.add(e);
		});
		Annotator.logger.debug("Added all entities");

		for (EntityGroup eg : JCasUtil.select(jcas, EntityGroup.class))
			for (int i = 0; i < eg.getMembers().size(); i++)
				cModel.insertNodeInto(new CATreeNode(eg.getMembers(i)), cModel.get(eg), 0);
		Annotator.logger.debug("Added all entity groups");

		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			cModel.addTo(cModel.get(m.getEntity()), cModel.add(m));
			cModel.registerAnnotation(m);
		}
		Annotator.logger.debug("Added all mentions");

		return cModel;
	}

	@Override
	protected CoreferenceModel doInBackground() throws Exception {
		return load(documentWindow, documentWindow.getMainApplication().getPreferences());
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