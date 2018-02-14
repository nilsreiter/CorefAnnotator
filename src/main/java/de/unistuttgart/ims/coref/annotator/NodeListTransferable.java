package de.unistuttgart.ims.coref.annotator;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

public class NodeListTransferable implements Transferable {
	public static DataFlavor dataFlavor = new DataFlavor(CATreeNode.class, "TreeNode");

	ImmutableList<CATreeNode> treeNodes;

	public NodeListTransferable(CATreeNode tn) {
		this.treeNodes = Lists.immutable.of(tn);
	}

	public NodeListTransferable(ImmutableList<CATreeNode> tn) {
		this.treeNodes = tn;
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
	public ImmutableList<CATreeNode> getTransferData(DataFlavor flavor) {
		return treeNodes;
	}

}
