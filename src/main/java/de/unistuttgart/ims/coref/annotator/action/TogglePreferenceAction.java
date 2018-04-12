package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.Ikon;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;

public abstract class TogglePreferenceAction extends IkonAction {
	private static final long serialVersionUID = 1L;

	String prefKey;
	boolean defaultValue;

	public TogglePreferenceAction(Annotator annotator, Ikon ikon, String stringKey, String prefKey, boolean def) {
		this(annotator, new Ikon[] { ikon }, stringKey, prefKey, def);
	}

	public TogglePreferenceAction(Annotator annotator, Ikon[] ikon, String stringKey, String prefKey, boolean def) {
		super(stringKey, ikon);
		putValue(Action.SELECTED_KEY, Annotator.app.getPreferences().getBoolean(prefKey, def));
		this.prefKey = prefKey;
		this.defaultValue = def;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean old = Annotator.app.getPreferences().getBoolean(prefKey, defaultValue);
		Annotator.app.getPreferences().putBoolean(prefKey, !old);
		putValue(Action.SELECTED_KEY, !old);
	}

	public static TogglePreferenceAction getAction(Annotator annotator, Ikon ikon, String stringKey, String prefKey,
			boolean def) {
		TogglePreferenceAction action = new TogglePreferenceAction(annotator, ikon, stringKey, prefKey, def) {
			private static final long serialVersionUID = 1L;
		};
		return action;
	}

	public static TogglePreferenceAction getAction(Annotator annotator, Constants.Setting<Boolean> setting) {
		TogglePreferenceAction action = new TogglePreferenceAction(annotator, setting.ikon,
				setting.toggleActionStringKey, setting.preferencesKey, setting.defaultValue) {
			private static final long serialVersionUID = 1L;
		};
		if (setting.getToggleActionTooltipKey() != null)
			action.putValue(Action.SHORT_DESCRIPTION, Annotator.getString(setting.toggleActionTooltipKey));
		return action;
	}

}
