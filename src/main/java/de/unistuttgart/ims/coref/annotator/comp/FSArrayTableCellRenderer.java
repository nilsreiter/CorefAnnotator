package de.unistuttgart.ims.coref.annotator.comp;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.apache.uima.jcas.cas.FSArray;

import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.document.DocumentModel;

public class FSArrayTableCellRenderer extends JPanel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;
	DefaultTableCellRenderer defCellRenderer = new DefaultTableCellRenderer();
	DocumentModel documentModel;

	public FSArrayTableCellRenderer(DocumentModel documentModel) {
		this.documentModel = documentModel;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		this.removeAll();
		JLabel label = (JLabel) defCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
				column);
		this.setBackground(label.getBackground());
		this.setForeground(label.getForeground());
		this.setBorder(label.getBorder());
		if (value != null) {
			FSArray arr = (FSArray) value;
			for (int i = 0; i < arr.size(); i++) {
				if (arr.get(i) instanceof Entity) {
					this.add(new EntityPanel(documentModel, (Entity) arr.get(i)));
				}
			}
		}
		return this;
	}

}