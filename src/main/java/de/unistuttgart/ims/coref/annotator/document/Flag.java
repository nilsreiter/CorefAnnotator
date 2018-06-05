package de.unistuttgart.ims.coref.annotator.document;

import javax.swing.Icon;

import org.apache.uima.jcas.cas.TOP;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

public class Flag {

	private static MutableMap<String, Flag> objectMap = Maps.mutable.empty();
	private static ImmutableSet<Flag> defaultSet = null;

	final String stringValue;
	Icon icon;
	Class<? extends TOP> targetClass;

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

	public Icon getIcon() {
		return icon;
	}

	public Flag setIcon(Icon icon) {
		this.icon = icon;
		return this;
	}

	public static ImmutableSet<Flag> getDefaultSet() {
		if (defaultSet == null) {
			defaultSet = Sets.immutable.with(
					getFlag(Constants.MENTION_FLAG_AMBIGUOUS, Mention.class)
							.setIcon(FontIcon.of(MaterialDesign.MDI_SHARE_VARIANT)),
					getFlag(Constants.MENTION_FLAG_DIFFICULT, Mention.class)
							.setIcon(FontIcon.of(MaterialDesign.MDI_ALERT_BOX)),
					getFlag(Constants.MENTION_FLAG_NON_NOMINAL, Mention.class)
							.setIcon(FontIcon.of(MaterialDesign.MDI_FLAG)));
		}
		return defaultSet;
	}
}
