package de.unistuttgart.ims.coref.annotator.comp;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import de.unistuttgart.ims.coref.annotator.api.v2.Entity;

public class EntityTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Entity e = (Entity) value;
		JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		c.setText(e.getLabel());
		c.setIcon(new ColorIcon(10, 10, new Color(e.getColor())));
		return c;
	}
}
