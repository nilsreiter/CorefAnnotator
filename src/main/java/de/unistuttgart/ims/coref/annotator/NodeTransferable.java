package de.unistuttgart.ims.coref.annotator;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public class NodeTransferable implements Transferable {
	public static DataFlavor dataFlavor = new DataFlavor(CATreeNode.class, "TreeNode");

	CATreeNode treeNode;

	public NodeTransferable(CATreeNode tn) {
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
