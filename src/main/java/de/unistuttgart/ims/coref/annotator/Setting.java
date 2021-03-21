package de.unistuttgart.ims.coref.annotator;

import org.kordamp.ikonli.Ikon;

public class Setting<T> {

	public T defaultValue;
	public Ikon[] ikon;
	public String preferencesKey;
	public String toggleActionStringKey;
	public String toggleActionTooltipKey;

	public Setting(String preferencesKey, String toggleActionKey, String toggleActionTooltipKey, T defaultValue,
			Ikon... ikon) {
		this.preferencesKey = preferencesKey;
		this.toggleActionStringKey = toggleActionKey;
		this.defaultValue = defaultValue;
		this.toggleActionTooltipKey = toggleActionTooltipKey;
		this.ikon = ikon;
	}

	public Setting(String preferencesKey, String toggleActionKey, T defaultValue, Ikon... ikon) {
		this.preferencesKey = preferencesKey;
		this.toggleActionStringKey = toggleActionKey;
		this.defaultValue = defaultValue;
		this.ikon = ikon;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public Ikon[] getIkon() {
		return ikon;
	}

	public String getPreferencesKey() {
		return preferencesKey;
	}

	public String getToggleActionStringKey() {
		return toggleActionStringKey;
	}

	public String getToggleActionTooltipKey() {
		return toggleActionTooltipKey;
	}

	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setIkon(Ikon[] ikon) {
		this.ikon = ikon;
	}

	public void setPreferencesKey(String preferencesKey) {
		this.preferencesKey = preferencesKey;
	}

	public void setToggleActionStringKey(String toggleActionStringKey) {
		this.toggleActionStringKey = toggleActionStringKey;
	}

	public void setToggleActionTooltipKey(String toggleActionTooltipKey) {
		this.toggleActionTooltipKey = toggleActionTooltipKey;
	}
}