package de.unistuttgart.ims.coref.annotator.document;

import java.util.UUID;
import java.util.prefs.Preferences;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.cas.FSList;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
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
		f.setUuid(key);

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

		ImmutableList<FeatureStructure> featureStructures = this.getFlaggedFeatureStructures(flag);
		operation.setFeatureStructures(featureStructures);

		featureStructures.select(fs -> UimaUtil.isX(fs, flag)).forEach(fs -> {
			Feature feature = fs.getType().getFeatureByBaseName("Flags");
			FSList<Flag> nArr = UimaUtil.removeFrom(UimaUtil.getFlags(fs), flag);
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
			op.setOldValue(flag.getUuid());
			flag.setUuid((String) op.getNewValue());
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

		return featureStructures.select(fs -> UimaUtil.isX(fs, flag));
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
			flag.setUuid((String) op.getOldValue());
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
			if (f.getUuid().equals(key))
				return f;
		return null;
	}

}
