package de.unistuttgart.ims.coref.annotator.document;

import org.apache.uima.jcas.cas.TOP;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class Flag {

	private static MutableMap<String, Flag> objectMap = Maps.mutable.empty();
	private static ImmutableSet<Flag> defaultSet = null;

	final String stringValue;
	Ikon ikon;
	Class<? extends TOP> targetClass;
	String translationKey;

	private Flag(String stringValue, Class<? extends TOP> cl) {
		this.stringValue = stringValue;
		this.targetClass = cl;
	}

	public static Flag getFlag(String s, Class<? extends TOP> cl) {
		if (!objectMap.containsKey(s)) {
			Flag f = new Flag(s, cl);
			objectMap.put(s, f);
		}
		return objectMap.get(s);
	}

	public String getStringValue() {
		return stringValue;
	}

	public Ikon getIcon() {
		return ikon;
	}

	public Flag setIkon(Ikon icon) {
		this.ikon = icon;
		return this;
	}

	public static ImmutableSet<Flag> getDefaultSet() {
		if (defaultSet == null) {
			defaultSet = Sets.immutable.with(
					getFlag(Constants.MENTION_FLAG_AMBIGUOUS, Mention.class).setIkon(MaterialDesign.MDI_SHARE_VARIANT)
							.setTranslationKey(Constants.Strings.MENTION_FLAG_AMBIGUOUS),
					getFlag(Constants.MENTION_FLAG_DIFFICULT, Mention.class).setIkon(MaterialDesign.MDI_ALERT_BOX)
							.setTranslationKey(Constants.Strings.MENTION_FLAG_DIFFICULT),
					getFlag(Constants.MENTION_FLAG_NON_NOMINAL, Mention.class).setIkon(MaterialDesign.MDI_FLAG)
							.setTranslationKey(Constants.Strings.MENTION_FLAG_NON_NOMINAL),
					getFlag(Constants.ENTITY_FLAG_GENERIC, Entity.class).setIkon(MaterialDesign.MDI_CLOUD)
							.setTranslationKey(Constants.Strings.ACTION_FLAG_ENTITY_GENERIC),
					getFlag(Constants.ENTITY_FLAG_HIDDEN, Entity.class).setIkon(MaterialDesign.MDI_ACCOUNT_OUTLINE)
							.setTranslationKey(Constants.Strings.ACTION_TOGGLE_ENTITY_VISIBILITY));
		}
		return defaultSet;
	}

	public String getTranslationKey() {
		return translationKey;
	}

	public Flag setTranslationKey(String translationKey) {
		this.translationKey = translationKey;
		return this;
	}

	public Class<? extends TOP> getTargetClass() {
		return targetClass;
	}
}
