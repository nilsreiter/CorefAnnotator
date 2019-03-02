package de.unistuttgart.ims.coref.annotator.comp;

import de.unistuttgart.ims.coref.annotator.AbstractTextWindow;
import de.unistuttgart.ims.coref.annotator.Span;

public class FixedTextLineNumber extends TextLineNumber {

	private static final long serialVersionUID = 1L;

	public FixedTextLineNumber(AbstractTextWindow component, int minimumDisplayDigits) {
		super(component, minimumDisplayDigits);
	}

	@Override
	protected String getTextLineNumber(int rowStartOffset, int rowEndOffset) {

		Integer n = textWindow.getDocumentModel().getLineNumber(new Span(rowStartOffset, rowEndOffset));
		if (n != null)
			return n.toString();
		else
			return "";

	}

}
