package de.unistuttgart.ims.coref.annotator;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class PotentialAnnotationTransfer implements Transferable {

	public static DataFlavor dataFlavor = new DataFlavor(PotentialAnnotationTransfer.class, "PotentialAnnotation");

	int begin;
	int end;
	DocumentWindow.CasTextView textView;

	public PotentialAnnotationTransfer(DocumentWindow.CasTextView tv, int begin, int end) {
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
