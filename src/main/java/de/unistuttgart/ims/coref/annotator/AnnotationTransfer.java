package de.unistuttgart.ims.coref.annotator;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.apache.uima.jcas.tcas.Annotation;

public class AnnotationTransfer<T extends Annotation> implements Transferable {
	public static DataFlavor dataFlavor = new DataFlavor(AnnotationTransfer.class, "Annotation");

	T annotation;
	CATreeNode treeNode;

	public AnnotationTransfer(T annotation, CATreeNode tn) {
		this.annotation = annotation;
		this.treeNode = tn;
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
		return annotation;
	}

	public CATreeNode getTreeNode() {
		return treeNode;
	}

}
