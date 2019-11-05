package de.unistuttgart.ims.coref.annotator.worker;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

import javax.swing.SwingWorker;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;
import de.unistuttgart.ims.coref.annotator.document.op.AddFlag;
import de.unistuttgart.ims.coref.annotator.document.op.AddMentionsToNewEntity;
import de.unistuttgart.ims.coref.annotator.document.op.Operation;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateEntityColor;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateEntityKey;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateEntityName;
import de.unistuttgart.ims.coref.annotator.profile.EntityType;
import de.unistuttgart.ims.coref.annotator.profile.FlagType;
import de.unistuttgart.ims.coref.annotator.profile.Profile;

public class DocumentModelLoader extends SwingWorker<DocumentModel, Integer> {

	Consumer<DocumentModel> consumer = null;
	JCas jcas;
	Profile profile;

	public DocumentModelLoader(Consumer<DocumentModel> consumer, JCas jcas) {
		this.consumer = consumer;
		this.jcas = jcas;
	}

	// TODO: Verify that entities / flags are not doubled
	// TODO: read only mode
	// TODO: Don't pollute the undo list
	protected DocumentModel load(Preferences preferences) {
		Annotator.logger.debug("Starting loading of coreference model");
		DocumentModel documentModel = new DocumentModel(jcas, preferences);
		documentModel.initialize();
		if (profile != null) {
			Annotator.logger.debug("Processing profile {}.", profile);
			for (FlagType ft : profile.getFlags().getFlag()) {
				try {
					String targetClassName = "de.unistuttgart.ims.coref.annotator.api.v1."
							+ ft.getTargetClass().value();

					Class<?> tClass = Class.forName(targetClassName);
					@SuppressWarnings("unchecked")
					AddFlag af = new AddFlag(ft.getUuid(), ft.getLabel(), MaterialDesign.valueOf(ft.getIcon()),
							(Class<? extends FeatureStructure>) tClass);
					documentModel.edit(af);
				} catch (ClassNotFoundException e) {
					Annotator.logger.catching(e);
				}
			}

			for (EntityType et : profile.getEntities().getEntity()) {
				AddMentionsToNewEntity op = new AddMentionsToNewEntity();
				documentModel.edit(op);
				documentModel.edit(new UpdateEntityName(op.getEntity(), et.getLabel()));
				documentModel.edit(new UpdateEntityColor(op.getEntity(), et.getColor()));
				documentModel.edit(new UpdateEntityKey(op.getEntity(), et.getShortcut().charAt(0)));

			}

			for (String operation : profile.getForbidden().getOperation()) {
				try {
					Class<? extends Operation> opClass = (Class<? extends Operation>) Class.forName(operation);
					documentModel.addBlockedOperation(opClass);

				} catch (ClassNotFoundException e) {
					Annotator.logger.catching(e);
				}
			}
		}

		return documentModel;
	}

	@Override
	protected DocumentModel doInBackground() throws Exception {
		DocumentModel documentModel = load(Annotator.app.getPreferences());
		return documentModel;
	}

	@Override
	protected void done() {
		try {
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

}