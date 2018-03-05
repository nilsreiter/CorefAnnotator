package de.unistuttgart.ims.coref.annotator;

import javax.swing.text.JTextComponent;

@Deprecated
public class PotentialAnnotation {
	int begin;
	int end;
	JTextComponent textPane;

	public PotentialAnnotation(JTextComponent textPane, int begin, int end) {
		this.begin = begin;
		this.end = end;
		this.textPane = textPane;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public JTextComponent getTextView() {
		return textPane;
	}

	public void setTextView(JTextComponent textPane) {
		this.textPane = textPane;
	}
}
