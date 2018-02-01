package de.unistuttgart.ims.coref.annotator;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class AnnotationTransfer implements Transferable {

	public static DataFlavor dataFlavor = new DataFlavor(AnnotationTransfer.class, "Annotation");

	int begin;
	int end;
	CasTextView textView;

	public AnnotationTransfer(CasTextView tv, int begin, int end) {
		this.textView = tv;
		this.begin = begin;
		this.end = end;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { dataFlavor };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor == dataFlavor;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		return new PotentialAnnotation(textView, begin, end);
	}

}
