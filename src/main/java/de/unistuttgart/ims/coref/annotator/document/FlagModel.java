package de.unistuttgart.ims.coref.annotator.document;

import java.util.prefs.Preferences;

import org.apache.uima.fit.util.JCasUtil;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class FlagModel {
	DocumentModel documentModel;

	public FlagModel(DocumentModel documentModel, Preferences preferences) {
		this.documentModel = documentModel;

		if (!JCasUtil.exists(documentModel.getJcas(), Flag.class)) {
			initialiseDefaultFlags();
		}
	}

	public void addFlag(String label) {

	}

	public ImmutableList<Flag> getFlags() {
		return Lists.immutable.withAll(JCasUtil.select(documentModel.getJcas(), Flag.class));
	}

	protected void initialiseDefaultFlags() {
		Flag flag;

		// ambiguous
		flag = new Flag(documentModel.getJcas());
		flag.setKey(Constants.MENTION_FLAG_AMBIGUOUS);
		flag.setLabel(Constants.Strings.MENTION_FLAG_AMBIGUOUS);
		flag.setIcon("MDI_SHARE_VARIANT");
		flag.setTargetClass(Mention.class.getName());
		flag.addToIndexes();

		// difficult
		flag = new Flag(documentModel.getJcas());
		flag.setKey(Constants.MENTION_FLAG_DIFFICULT);
		flag.setLabel(Constants.Strings.MENTION_FLAG_DIFFICULT);
		flag.setIcon("MDI_ALERT_BOX");
		flag.setTargetClass(Mention.class.getName());
		flag.addToIndexes();

		// non-nominal
		flag = new Flag(documentModel.getJcas());
		flag.setKey(Constants.MENTION_FLAG_NON_NOMINAL);
		flag.setLabel(Constants.Strings.MENTION_FLAG_NON_NOMINAL);
		flag.setIcon("MDI_FLAG");
		flag.setTargetClass(Mention.class.getName());
		flag.addToIndexes();

		// generic
		flag = new Flag(documentModel.getJcas());
		flag.setKey(Constants.ENTITY_FLAG_GENERIC);
		flag.setLabel(Constants.Strings.ACTION_FLAG_ENTITY_GENERIC);
		flag.setIcon("MDI_CLOUD");
		flag.setTargetClass(Entity.class.getName());
		flag.addToIndexes();

		// hidden
		flag = new Flag(documentModel.getJcas());
		flag.setKey(Constants.ENTITY_FLAG_HIDDEN);
		flag.setLabel(Constants.Strings.ACTION_TOGGLE_ENTITY_VISIBILITY);
		flag.setIcon("MDI_ACCOUNT_OUTLINE");
		flag.setTargetClass(Entity.class.getName());
		flag.addToIndexes();
	}

	public String getLocalizedLabel(Flag f) {
		return Annotator.getString(f.getLabel());
	}

	public String getLabel(Flag f) {
		return f.getLabel();
	}

	public Ikon getIkon(Flag f) {
		return MaterialDesign.valueOf(f.getIcon());
	}

}
