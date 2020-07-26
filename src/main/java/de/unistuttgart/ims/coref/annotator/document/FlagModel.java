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
import de.unistuttgart.ims.coref.annotator.Defaults;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.api.v2.Entity;
import de.unistuttgart.ims.coref.annotator.api.v2.Flag;
import de.unistuttgart.ims.coref.annotator.api.v2.Mention;
import de.unistuttgart.ims.coref.annotator.document.Event.Type;
import de.unistuttgart.ims.coref.annotator.document.op.AddFlag;
import de.unistuttgart.ims.coref.annotator.document.op.DeleteFlag;
import de.unistuttgart.ims.coref.annotator.document.op.FlagModelOperation;
import de.unistuttgart.ims.coref.annotator.document.op.ToggleGenericFlag;
import de.unistuttgart.ims.coref.annotator.document.op.UpdateFlag;
import de.unistuttgart.ims.coref.annotator.uima.UimaUtil;

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
public class FlagModel extends SubModel implements Model {
	private MutableSet<String> keys = Sets.mutable.empty();
	MutableSet<FlagModelListener> listeners = Sets.mutable.empty();

	public FlagModel(DocumentModel documentModel, Preferences preferences) {
		super(documentModel);

		if (preferences.getBoolean(Constants.CFG_CREATE_DEFAULT_FLAGS, Defaults.CFG_CREATE_DEFAULT_FLAGS)
				&& !JCasUtil.exists(documentModel.getJcas(), Flag.class)) {
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
		else if (fmo instanceof UpdateFlag)
			edit((UpdateFlag) fmo);
		else
			throw new UnsupportedOperationException();

	}

	protected void edit(AddFlag operation) {
		Flag f = new Flag(documentModel.getJcas());
		f.addToIndexes();

		if (operation.getLabel() != null)
			f.setLabel(operation.getLabel());
		else
			f.setLabel(Annotator.getString(Strings.FLAGMODEL_NEW_FLAG));

		String key = UUID.randomUUID().toString();
		if (operation.getKey() != null)
			key = operation.getKey();
		while (keys.contains(key)) {
			key = UUID.randomUUID().toString();
		}
		f.setKey(key);

		if (operation.getIcon() != null)
			f.setIcon(operation.getIcon().toString());
		else
			f.setIcon(Util.randomEnum(MaterialDesign.class).toString());

		if (operation.getTargetClass() != null)
			f.setTargetClass(operation.getTargetClass().getName());
		else
			f.setTargetClass(Entity.class.getName());

		f.addToIndexes();
		keys.add(key);

		operation.setAddedFlag(f);
		documentModel.fireDocumentChangedEvent();
		fireFlagEvent(Event.get(this, Type.Add, f));
	}

	protected void edit(DeleteFlag operation) {
		Flag flag = operation.getFlag();

		if (flag.getKey().equals(Constants.ENTITY_FLAG_GENERIC) || flag.getKey().equals(Constants.ENTITY_FLAG_HIDDEN)
				|| flag.getKey().equals(Constants.MENTION_FLAG_AMBIGUOUS)
				|| flag.getKey().equals(Constants.MENTION_FLAG_DIFFICULT)
				|| flag.getKey().equals(Constants.MENTION_FLAG_NON_NOMINAL))
			return;
		ImmutableList<FeatureStructure> featureStructures = this.getFlaggedFeatureStructures(flag);
		operation.setFeatureStructures(featureStructures);

		featureStructures.select(fs -> UimaUtil.isX(fs, flag.getKey())).forEach(fs -> {
			Feature feature = fs.getType().getFeatureByBaseName("Flags");
			StringArray nArr = UimaUtil.removeFrom(documentModel.getJcas(), (StringArray) fs.getFeatureValue(feature),
					flag.getKey());
			((StringArray) fs.getFeatureValue(feature)).removeFromIndexes();
			fs.setFeatureValue(feature, nArr);
		});

		flag.removeFromIndexes();
		fireFlagEvent(Event.get(this, Type.Remove, flag));

	}

	protected void edit(UpdateFlag op) {
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
		case KEY:
			op.setOldValue(flag.getKey());
			getFlaggedFeatureStructures(flag).forEach(fs -> {
				UimaUtil.removeFlagKey(fs, (String) op.getOldValue());
				UimaUtil.addFlagKey(fs, (String) op.getNewValue());
			});
			flag.setKey((String) op.getNewValue());
			break;
		}
		Annotator.logger.traceEntry();
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
			} else if (getTargetClass(flag) == Entity.class) {
				featureStructures = Lists.immutable.ofAll(JCasUtil.select(documentModel.getJcas(), Entity.class));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return featureStructures.select(fs -> UimaUtil.isX(fs, flag.getKey()));
	}

	@Deprecated
	protected void initialiseDefaultFlags() {
		Flag flag;

		// ambiguous
		flag = new Flag(documentModel.getJcas());
		flag.setKey(Constants.MENTION_FLAG_AMBIGUOUS);
		flag.setLabel(Strings.MENTION_FLAG_AMBIGUOUS);
		flag.setIcon("MDI_SHARE_VARIANT");
		flag.setTargetClass(Mention.class.getName());
		flag.addToIndexes();
		keys.add(Constants.MENTION_FLAG_AMBIGUOUS);

		// difficult
		flag = new Flag(documentModel.getJcas());
		flag.setKey(Constants.MENTION_FLAG_DIFFICULT);
		flag.setLabel(Strings.MENTION_FLAG_DIFFICULT);
		flag.setIcon("MDI_ALERT_BOX");
		flag.setTargetClass(Mention.class.getName());
		flag.addToIndexes();
		keys.add(Constants.MENTION_FLAG_DIFFICULT);

		// non-nominal
		flag = new Flag(documentModel.getJcas());
		flag.setKey(Constants.MENTION_FLAG_NON_NOMINAL);
		flag.setLabel(Strings.MENTION_FLAG_NON_NOMINAL);
		flag.setIcon("MDI_FLAG");
		flag.setTargetClass(Mention.class.getName());
		flag.addToIndexes();
		keys.add(Constants.MENTION_FLAG_NON_NOMINAL);

		// generic
		flag = new Flag(documentModel.getJcas());
		flag.setKey(Constants.ENTITY_FLAG_GENERIC);
		flag.setLabel(Strings.ACTION_FLAG_ENTITY_GENERIC);
		flag.setIcon("MDI_CLOUD");
		flag.setTargetClass(Entity.class.getName());
		flag.addToIndexes();
		keys.add(Constants.ENTITY_FLAG_GENERIC);

		// hidden
		flag = new Flag(documentModel.getJcas());
		flag.setKey(Constants.ENTITY_FLAG_HIDDEN);
		flag.setLabel(Strings.ACTION_TOGGLE_ENTITY_VISIBILITY);
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
		e.flagEvent(Event.get(this, Event.Type.Init));
		return listeners.add(e);
	}

	public boolean removeFlagModelListener(Object o) {
		return listeners.remove(o);
	}

	protected void undo(FlagModelOperation fmo) {
		if (fmo instanceof AddFlag)
			undo((AddFlag) fmo);
		else if (fmo instanceof DeleteFlag)
			undo((DeleteFlag) fmo);
		else if (fmo instanceof UpdateFlag)
			undo((UpdateFlag) fmo);
		else
			throw new UnsupportedOperationException();
	}

	protected void undo(AddFlag fmo) {
		edit(new DeleteFlag(fmo.getAddedFlag()));
		fmo.setAddedFlag(null);
	}

	protected void undo(DeleteFlag fmo) {
		fmo.getFlag().addToIndexes();
		documentModel.getCoreferenceModel().edit(new ToggleGenericFlag(fmo.getFlag(), fmo.getFeatureStructures()));

		fireFlagEvent(Event.get(this, Type.Add, fmo.getFlag()));
		documentModel.getCoreferenceModel().fireEvent(Event.get(this, Event.Type.Update, fmo.getFeatureStructures()));

	}

	protected void undo(UpdateFlag op) {
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
		case KEY:
			getFlaggedFeatureStructures(flag).forEach(fs -> {
				UimaUtil.removeFlagKey(fs, (String) op.getNewValue());
				UimaUtil.addFlagKey(fs, (String) op.getOldValue());
			});
			flag.setKey((String) op.getOldValue());
			break;
		}
		updateFlag(flag);
	}

	public void updateFlag(Flag flag) {
		Annotator.logger.traceEntry();
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
