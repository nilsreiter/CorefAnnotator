package de.unistuttgart.ims.coref.annotator.worker;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

import javax.swing.SwingWorker;

import org.apache.uima.jcas.JCas;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.profile.Profile;

public class DocumentModelLoader extends SwingWorker<DocumentModel, Integer> {

	Consumer<DocumentModel> consumer = null;
	JCas jcas;
	Profile profile;
	Preferences preferences;

	public DocumentModelLoader(Consumer<DocumentModel> consumer, JCas jcas) {
		this.consumer = consumer;
		this.jcas = jcas;
	}

	protected DocumentModel load(Preferences preferences) {
		Annotator.logger.debug("Starting loading of coreference model");
		DocumentModel documentModel = new DocumentModel(jcas, preferences);
		documentModel.initialize();
		if (profile != null)
			documentModel.loadProfile(profile);
		return documentModel;
	}

	@Override
	protected DocumentModel doInBackground() throws Exception {
		DocumentModel documentModel = load(preferences == null ? Annotator.app.getPreferences() : preferences);
		return documentModel;
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

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

}