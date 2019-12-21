package de.unistuttgart.ims.coref.annotator.plugins;

import java.awt.Component;
import java.util.prefs.Preferences;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;

import de.unistuttgart.ims.coref.annotator.Annotator;

public abstract class PluginOption {
	public static class IntegerPluginOption extends PluginOption {
		JSpinner component;

		public IntegerPluginOption(Preferences preferences, String preferencesKey, int defaultValue, String stringKey,
				String tooltipKey, int min, int max, int step) {
			super(preferences, preferencesKey, defaultValue, stringKey, tooltipKey);
			component = new JSpinner(
					new SpinnerNumberModel(preferences.getInt((preferencesKey), defaultValue), min, max, step));

		}

		@Override
		public Component getComponent() {
			return component;
		}

		@Override
		public void ok() {
			preferences.putInt(preferencesKey, ((SpinnerNumberModel) component.getModel()).getNumber().intValue());

		}
	}

	public static class BooleanPluginOption extends PluginOption {

		JCheckBox checkBox;

		public BooleanPluginOption(Preferences preferences, String preferencesKey, Object defaultValue,
				String stringKey, String tooltipKey) {
			super(preferences, preferencesKey, defaultValue, stringKey, tooltipKey);
			checkBox = new JCheckBox();
			checkBox.setSelected(preferences.getBoolean(preferencesKey, (boolean) defaultValue));
		}

		@Override
		public Component getComponent() {
			return checkBox;
		}

		@Override
		public void ok() {
			preferences.putBoolean(preferencesKey, checkBox.isSelected());
		};

	}

	public static class EnumPluginOption<T extends Enum<T>> extends PluginOption {

		JComboBox<T> component;

		public EnumPluginOption(Class<T> enumClass, Preferences preferences, String preferencesKey, T defaultValue,
				String stringKey, String tooltipKey, T[] availableValues, ListCellRenderer<Object> cellRenderer) {
			super(preferences, preferencesKey, defaultValue, stringKey, tooltipKey);
			component = new JComboBox<T>(availableValues);
			if (cellRenderer != null)
				component.setRenderer(cellRenderer);

			String selectedAsString = preferences.get(preferencesKey, defaultValue.name());
			for (T t : enumClass.getEnumConstants()) {
				if (t.name().equals(selectedAsString))
					component.setSelectedItem(t);
			}
		}

		@Override
		public void setValues(Object values) {
		};

		@Override
		public Component getComponent() {
			return component;
		}

		@Override
		public void ok() {
			@SuppressWarnings("unchecked")
			T unit = (T) component.getSelectedItem();
			preferences.put(preferencesKey, unit.name());
		};

	}

	@Deprecated
	public static <X> PluginOption getPluginOption(Class<X> dataType, Preferences preferences, String preferencesKey,
			Object defaultValue, String stringKey, String tooltipKey) {
		if (dataType == Boolean.class)
			return new BooleanPluginOption(preferences, preferencesKey, defaultValue, stringKey, tooltipKey);
		throw new UnsupportedOperationException();
	}

	String preferencesKey;
	Object defaultValue;
	String stringKey;

	String tooltipKey;

	Preferences preferences;

	private PluginOption(Preferences preferences, String preferencesKey, Object defaultValue, String stringKey,
			String tooltipKey) {
		super();
		this.preferences = preferences;
		this.preferencesKey = preferencesKey;
		this.defaultValue = defaultValue;
		this.stringKey = stringKey;
		this.tooltipKey = tooltipKey;
	}

	public abstract Component getComponent();

	public void setValues(Object values) {
	};

	public JLabel getLabel() {
		JLabel lab = new JLabel(Annotator.getString(stringKey));
		lab.setToolTipText(Annotator.getString(tooltipKey));
		return lab;
	};

	public abstract void ok();
}
