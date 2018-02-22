package de.unistuttgart.ims.coref.annotator.worker;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CATreeNode;
import de.unistuttgart.ims.coref.annotator.CoreferenceModel;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class CoreferenceModelLoader extends SwingWorker<CoreferenceModel, Integer> {

	private DocumentWindow documentWindow;
	JCas jcas;

	public CoreferenceModelLoader(DocumentWindow documentWindow, JCas jcas) {
		this.documentWindow = documentWindow;
		this.jcas = jcas;
	}

	@Override
	protected CoreferenceModel doInBackground() throws Exception {
		CoreferenceModel cModel;
		cModel = new CoreferenceModel(jcas, documentWindow.getMainApplication().getPreferences());
		cModel.addCoreferenceModelListener(documentWindow);

		Lists.immutable.withAll(JCasUtil.select(jcas, Entity.class)).forEach(e -> {
			cModel.add(e);
		});

		publish(60);
		for (EntityGroup eg : JCasUtil.select(jcas, EntityGroup.class))
			for (int i = 0; i < eg.getMembers().size(); i++)
				cModel.insertNodeInto(new CATreeNode(eg.getMembers(i)), cModel.get(eg), 0);

		publish(70);
		for (Mention m : JCasUtil.select(jcas, Mention.class)) {
			cModel.addTo(cModel.get(m.getEntity()), cModel.add(m));
			cModel.registerAnnotation(m);
		}
		publish(75);
		return cModel;
	}

	@Override
	protected void done() {
		try {
			documentWindow.fireModelCreatedEvent(get());
		} catch (InterruptedException | ExecutionException e) {
			Annotator.logger.catching(e);
		}
	}

}