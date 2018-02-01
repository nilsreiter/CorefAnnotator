package de.unistuttgart.ims.coref.annotator;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import org.apache.uima.cas.FeatureStructure;

public class NodeTransferable<T extends FeatureStructure> implements Transferable {
	public static DataFlavor dataFlavor = new DataFlavor(TreeNode.class, "TreeNode");

	TreeNode<T> treeNode;

	public NodeTransferable(TreeNode<T> tn) {
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
	public Object getTransferData(DataFlavor flavor) {
		return treeNode;
	}

}
