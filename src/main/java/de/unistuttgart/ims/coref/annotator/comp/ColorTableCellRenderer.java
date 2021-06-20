package de.unistuttgart.ims.coref.annotator.comp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ColorTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		ColorIcon color = new ColorIcon(20, 10, (Color) value);
		color.setBorderColor((Color) value);
		color.setInsets(new Insets(0, 0, 0, 0));

		JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		c.setText(null);
		c.setIcon(color);
		return c;
	}
}