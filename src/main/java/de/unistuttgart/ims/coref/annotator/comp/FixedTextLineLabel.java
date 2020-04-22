package de.unistuttgart.ims.coref.annotator.comp;

import de.unistuttgart.ims.coref.annotator.AbstractTextWindow;
import de.unistuttgart.ims.coref.annotator.Span;

public class FixedTextLineLabel extends TextLineNumber {

	private static final long serialVersionUID = 1L;

	public FixedTextLineLabel(AbstractTextWindow component, int minimumDisplayDigits) {
		super(component, minimumDisplayDigits);
	}

	@Override
	protected String getTextLineNumber(int rowStartOffset, int rowEndOffset) {
		return textWindow.getDocumentModel().getLineLabel(new Span(rowStartOffset, rowEndOffset));
	}

}
