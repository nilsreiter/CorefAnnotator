package de.unistuttgart.ims.coref.annotator.comp;

import java.awt.Window;
import java.util.function.BooleanSupplier;

import javax.swing.JOptionPane;

public class ImprovedMessageDialog {
	public static void showMessageDialog(Window parent, String title, String message, String[] optionLabels,
			BooleanSupplier[] functions) {

		int r = JOptionPane.showOptionDialog(parent, message, title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.WARNING_MESSAGE, null, optionLabels, optionLabels[0]);
		functions[r].getAsBoolean();

	}
}
