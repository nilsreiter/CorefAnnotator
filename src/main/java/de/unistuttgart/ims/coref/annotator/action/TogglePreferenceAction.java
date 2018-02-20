package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.Ikon;

import de.unistuttgart.ims.coref.annotator.Annotator;

public abstract class TogglePreferenceAction extends AnnotatorAction {
	private static final long serialVersionUID = 1L;

	String prefKey;
	boolean defaultValue;

	public TogglePreferenceAction(Annotator annotator, Ikon ikon, String stringKey, String prefKey, boolean def) {
		super(annotator, ikon, stringKey);
		putValue(Action.SELECTED_KEY, mainApplication.getPreferences().getBoolean(prefKey, def));
		this.prefKey = prefKey;
		this.defaultValue = def;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean old = mainApplication.getPreferences().getBoolean(prefKey, defaultValue);
		mainApplication.getPreferences().putBoolean(prefKey, !old);
		putValue(Action.SELECTED_KEY, !old);
	}

	public static TogglePreferenceAction getAction(Annotator annotator, Ikon ikon, String stringKey, String prefKey,
			boolean def) {
		TogglePreferenceAction action = new TogglePreferenceAction(annotator, ikon, stringKey, prefKey, def) {
			private static final long serialVersionUID = 1L;
		};
		return action;
	}

}
