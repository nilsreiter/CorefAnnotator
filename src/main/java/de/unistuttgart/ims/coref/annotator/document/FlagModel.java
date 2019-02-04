package de.unistuttgart.ims.coref.annotator.document;

import java.util.UUID;
import java.util.prefs.Preferences;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.cas.StringArray;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.api.v1.DetachedMentionPart;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.document.Event.Type;
import de.unistuttgart.ims.coref.annotator.document.op.AddFlag;
import de.unistuttgart.ims.coref.annotator.document.op.DeleteFlag;
import de.unistuttgart.ims.coref.annotator.document.op.FlagModelOperation;
import de.unistuttgart.ims.coref.annotator.document.op.SetFlagProperty;

/**
 * <h2>Mapping of features to columns</h2>
 * <table>
 * <tr>
 * <th>Column</th>
 * <th>Feature</th>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>Icon</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>Key</td>
 * </tr>
 * <tr>
 * <td>3</td>
 * <td>Label</td>
 * </tr>
 * <tr>
 * <td>4</td>
 * <td>TargetClass</td>
 * </tr>
 * </table>
 * 
 * @author reiterns
 *
 */
public class FlagModel implements Model {
	DocumentModel documentModel;
	private MutableSet<String> keys = Sets.mutable.empty();
	MutableSet<FlagModelListener> listeners = Sets.mutable.empty();

	public FlagModel(DocumentModel documentModel, Preferences preferences) {
		this.documentModel = documentModel;

		if (!JCasUtil.exists(documentModel.getJcas(), Flag.class)) {
			initialiseDefaultFlags();
		}
	}

	@Deprecated
	protected void addFlag(String label, Class<? extends FeatureStructure> targetClass) {
		addFlag(label, targetClass, null);
	}

	@Deprecated
	protected synchronized void addFlag(String label, Class<? extends FeatureStructure> targetClass, Ikon ikon) {
		Flag f = new Flag(documentModel.getJcas());
		f.addToIndexes();
		f.setLabel(label);
		String key = UUID.randomUUID().toString();
		while (keys.contains(key)) {
			key = UUID.randomUUID().toString();
		}
		f.setKey(key);

		if (ikon != null)
			f.setIcon(ikon.toString());
		f.setTargetClass(targetClass.getName());
		f.addToIndexes();
		keys.add(key);
		fireFlagEvent(Event.get(this, Type.Add, f));
	}

	protected void edit(FlagModelOperation fmo) {
		if (fmo instanceof AddFlag)
			edit((AddFlag) fmo);
		else if (fmo instanceof DeleteFlag)
			edit((DeleteFlag) fmo);
		else if (fmo instanceof SetFlagProperty)
			edit((SetFlagProperty) fmo);
		else
			throw new UnsupportedOperationException();

	}

	protected void edit(AddFlag operation) {
		Flag f = new Flag(documentModel.getJcas());
		f.addToIndexes();
		f.setLabel("New Flag");
		String key = UUID.randomUUID().toString();
		while (keys.contains(key)) {
			key = UUID.randomUUID().toString();
		}
		f.setKey(key);
		f.setIcon(Util.randomEnum(MaterialDesign.class).toString());
		f.setTargetClass(operation.getTargetClass().getName());
		f.addToIndexes();
		keys.add(key);

		operation.setAddedFlag(f);
		fireFlagEvent(Event.get(this, Type.Add, f));
		documentModel.fireDocumentChangedEvent();
	}

	protected void edit(DeleteFlag operation) {
		Flag flag = operation.getFlag();
		fireFlagEvent(Event.get(this, Type.Remove, flag));

		if (flag.getKey().equals(Constants.ENTITY_FLAG_GENERIC) || flag.getKey().equals(Constants.ENTITY_FLAG_HIDDEN)
				|| flag.getKey().equals(Constants.MENTION_FLAG_AMBIGUOUS)
				|| flag.getKey().equals(Constants.MENTION_FLAG_DIFFICULT)
				|| flag.getKey().equals(Constants.MENTION_FLAG_NON_NOMINAL))
			return;
		ImmutableList<FeatureStructure> featureStructures = this.getFlaggedFeatureStructures(flag);
		operation.setFeatureStructures(featureStructures);

		featureStructures.select(fs -> Util.isX(fs, flag.getKey())).forEach(fs -> {
			Feature feature = fs.getType().getFeatureByBaseName("Flags");
			StringArray nArr = Util.removeFrom(documentModel.getJcas(), (StringArray) fs.getFeatureValue(feature),
					flag.getKey());
			((StringArray) fs.getFeatureValue(feature)).removeFromIndexes();
			fs.setFeatureValue(feature, nArr);
		});

		flag.removeFromIndexes();
	}

	protected void edit(SetFlagProperty op) {
		Flag flag = op.getFlag();
		switch (op.getFlagProperty()) {
		case LABEL:
			op.setOldValue(flag.getLabel());
			flag.setLabel((String) op.getNewValue());
			break;
		case ICON:
			op.setOldValue(flag.getIcon());
			flag.setIcon((String) op.getNewValue());
			break;
		case TARGETCLASS:
			op.setOldValue(flag.getTargetClass());
			flag.setTargetClass((String) op.getNewValue());
			break;
		}
		Annotator.logger.entry(op);
		fireFlagEvent(Event.get(this, Event.Type.Update, flag));
	}

	private void fireFlagEvent(FeatureStructureEvent evt) {
		listeners.forEach(l -> l.flagEvent(evt));
	}

	public ImmutableList<Flag> getFlags() {
		return Lists.immutable.withAll(JCasUtil.select(documentModel.getJcas(), Flag.class));
	}

	protected ImmutableList<FeatureStructure> getFlaggedFeatureStructures(Flag flag) {
		ImmutableList<FeatureStructure> featureStructures = Lists.immutable.empty();
		try {
			if (getTargetClass(flag) == Mention.class) {
				featureStructures = Lists.immutable.ofAll(JCasUtil.select(documentModel.getJcas(), Mention.class));
			} else if (getTargetClass(flag) == DetachedMentionPart.class) {
				featureStructures = Lists.immutable
						.ofAll(JCasUtil.select(documentModel.getJcas(), DetachedMentionPart.class));
			} else if (getTargetClass(flag) == Entity.class) {
				featureStructures = Lists.immutable.ofAll(JCasUtil.select(documentModel.getJcas(), Entity.class));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return featureStructures.select(fs -> Util.isX(fs, flag.getKey()));
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
		keys.add(Constants.MENTION_FLAG_AMBIGUOUS);

		// difficult
		flag = new Flag(documentModel.getJcas());
		flag.setKey(Constants.MENTION_FLAG_DIFFICULT);
		flag.setLabel(Constants.Strings.MENTION_FLAG_DIFFICULT);
		flag.setIcon("MDI_ALERT_BOX");
		flag.setTargetClass(Mention.class.getName());
		flag.addToIndexes();
		keys.add(Constants.MENTION_FLAG_DIFFICULT);

		// non-nominal
		flag = new Flag(documentModel.getJcas());
		flag.setKey(Constants.MENTION_FLAG_NON_NOMINAL);
		flag.setLabel(Constants.Strings.MENTION_FLAG_NON_NOMINAL);
		flag.setIcon("MDI_FLAG");
		flag.setTargetClass(Mention.class.getName());
		flag.addToIndexes();
		keys.add(Constants.MENTION_FLAG_NON_NOMINAL);

		// generic
		flag = new Flag(documentModel.getJcas());
		flag.setKey(Constants.ENTITY_FLAG_GENERIC);
		flag.setLabel(Constants.Strings.ACTION_FLAG_ENTITY_GENERIC);
		flag.setIcon("MDI_CLOUD");
		flag.setTargetClass(Entity.class.getName());
		flag.addToIndexes();
		keys.add(Constants.ENTITY_FLAG_GENERIC);

		// hidden
		flag = new Flag(documentModel.getJcas());
		flag.setKey(Constants.ENTITY_FLAG_HIDDEN);
		flag.setLabel(Constants.Strings.ACTION_TOGGLE_ENTITY_VISIBILITY);
		flag.setIcon("MDI_ACCOUNT_OUTLINE");
		flag.setTargetClass(Entity.class.getName());
		flag.addToIndexes();
		keys.add(Constants.ENTITY_FLAG_HIDDEN);
	}

	public Class<?> getTargetClass(Flag f) throws ClassNotFoundException {
		return Class.forName(f.getTargetClass());
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

	public boolean addFlagModelListener(FlagModelListener e) {
		return listeners.add(e);
	}

	public boolean removeFlagModelListener(Object o) {
		return listeners.remove(o);
	}

	protected void undo(FlagModelOperation fmo) {
		if (fmo instanceof AddFlag)
			undo((AddFlag) fmo);
		else if (fmo instanceof DeleteFlag)
			undo(fmo);
		else if (fmo instanceof SetFlagProperty)
			undo((SetFlagProperty) fmo);
		else
			throw new UnsupportedOperationException();
	}

	protected void undo(AddFlag fmo) {
		edit(new DeleteFlag(fmo.getAddedFlag()));
		fmo.setAddedFlag(null);
	}

	protected void undo(SetFlagProperty op) {
		Flag flag = op.getFlag();
		switch (op.getFlagProperty()) {
		case LABEL:
			flag.setLabel((String) op.getOldValue());
			break;
		case ICON:
			flag.setIcon((String) op.getOldValue());
			break;
		case TARGETCLASS:
			flag.setTargetClass((String) op.getOldValue());
			break;
		}
		updateFlag(flag);
	}

	public void updateFlag(Flag flag) {
		Annotator.logger.entry(flag);
		fireFlagEvent(Event.get(this, Event.Type.Update, flag));
		documentModel.getCoreferenceModel()
				.fireEvent(Event.get(this, Event.Type.Update, getFlaggedFeatureStructures(flag)));
	}

	public Flag getFlag(String key) {
		for (Flag f : getFlags())
			if (f.getKey().equals(key))
				return f;
		return null;
	}

}
