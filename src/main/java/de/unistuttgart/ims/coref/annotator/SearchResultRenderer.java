package de.unistuttgart.ims.coref.annotator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import de.unistuttgart.ims.coref.annotator.api.v1.Mention;

class SearchResultRenderer<T extends SearchResult> implements ListCellRenderer<T> {

	Font contextFont;
	Font centerFont;
	String text;
	int contexts;

	public SearchResultRenderer(String text, int contexts) {
		this.text = text;
		this.contexts = contexts;
		contextFont = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
		centerFont = new Font(Font.SANS_SERIF, Font.BOLD, 13);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected,
			boolean cellHasFocus) {

		JPanel panel = new JPanel();
		if (isSelected) {
			panel.setBackground(list.getSelectionBackground());
			panel.setForeground(list.getSelectionForeground());
		} else {
			panel.setBackground(list.getBackground());
			panel.setForeground(list.getForeground());
		}
		JLabel left = new JLabel(
				text.substring(Integer.max(value.getSpan().begin - contexts, 0), value.getSpan().begin));
		JLabel right = new JLabel(
				text.substring(value.getSpan().end, Integer.min(value.getSpan().end + contexts, text.length() - 1)));
		left.setFont(contextFont);
		right.setFont(contextFont);

		JLabel center = new JLabel(text.substring(value.getSpan().begin, value.getSpan().end));
		center.setFont(centerFont);
		if (value instanceof SearchResultMention) {
			Mention m = ((SearchResultMention) value).getMention();
			center.setForeground(new Color(m.getEntity().getColor()));
		}
		panel.add(left);
		panel.add(center);
		panel.add(right);

		return panel;
	}

}