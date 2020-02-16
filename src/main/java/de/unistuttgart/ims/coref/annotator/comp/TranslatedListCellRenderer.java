package de.unistuttgart.ims.coref.annotator.comp;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import de.unistuttgart.ims.coref.annotator.Annotator;

public class TranslatedListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;

	String prefix = "";

	public TranslatedListCellRenderer(String prefix) {
		super();
		this.prefix = prefix;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
			boolean isSelected, boolean cellHasFocus) {
		JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		l.setText(Annotator.getString(prefix + value.toString()));
		return l;
	}

}
