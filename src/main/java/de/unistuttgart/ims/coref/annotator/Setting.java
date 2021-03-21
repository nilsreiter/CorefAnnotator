package de.unistuttgart.ims.coref.annotator;

import java.util.function.Consumer;

import org.kordamp.ikonli.Ikon;

public class Setting<T> {

	T defaultValue;
	Ikon[] ikon;
	String preferencesKey;
	String toggleActionStringKey;
	String toggleActionTooltipKey;
	Consumer<Annotator> toggleActionCode;

	public Setting(String preferencesKey, String toggleActionKey, String toggleActionTooltipKey, T defaultValue,
			Consumer<Annotator> toggleAction, Ikon... ikon) {
		this.preferencesKey = preferencesKey;
		this.toggleActionStringKey = toggleActionKey;
		this.defaultValue = defaultValue;
		this.toggleActionTooltipKey = toggleActionTooltipKey;
		this.ikon = ikon;
		this.toggleActionCode = toggleAction;
	}

	public Setting(String preferencesKey, String toggleActionKey, String toggleActionTooltipKey, T defaultValue,
			Ikon... ikon) {
		this(preferencesKey, toggleActionKey, toggleActionTooltipKey, defaultValue, null, ikon);
	}

	public Setting(String preferencesKey, String toggleActionKey, T defaultValue, Ikon... ikon) {
		this(preferencesKey, toggleActionKey, null, defaultValue, null, ikon);
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