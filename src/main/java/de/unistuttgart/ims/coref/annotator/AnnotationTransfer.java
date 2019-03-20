package de.unistuttgart.ims.coref.annotator;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.apache.uima.jcas.tcas.Annotation;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

public class AnnotationTransfer implements Transferable {
	public static DataFlavor dataFlavor = new DataFlavor(AnnotationTransfer.class, "Annotation");

	CATreeNode treeNode;
	ImmutableList<Annotation> annotationList;

	public AnnotationTransfer(Annotation annotation, CATreeNode tn) {
		this.treeNode = tn;
		this.annotationList = Lists.immutable.of(annotation);
	}

	public AnnotationTransfer(Iterable<Annotation> annotations) {
		this.annotationList = Lists.immutable.withAll(annotations);
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
		return annotationList;
	}

	public CATreeNode getTreeNode() {
		return treeNode;
	}

}
