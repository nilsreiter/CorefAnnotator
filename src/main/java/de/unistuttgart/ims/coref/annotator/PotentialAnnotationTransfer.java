package de.unistuttgart.ims.coref.annotator;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.text.JTextComponent;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

public class PotentialAnnotationTransfer implements Transferable {

	public static DataFlavor dataFlavor = new DataFlavor(PotentialAnnotationTransfer.class, "PotentialAnnotation");

	JTextComponent textView;
	ImmutableList<Span> list;

	public PotentialAnnotationTransfer(JTextComponent tv, int begin, int end) {
		this.textView = tv;
		this.list = Lists.immutable.of(new Span(begin, end));
	}

	public PotentialAnnotationTransfer(JTextComponent tv, Iterable<Span> iterable) {
		this.textView = tv;
		this.list = Lists.immutable.withAll(iterable);
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
	public ImmutableList<Span> getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		return list;
		// return new PotentialAnnotation(textView, begin, end);
	}

}
