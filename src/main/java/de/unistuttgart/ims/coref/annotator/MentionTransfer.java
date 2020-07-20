package de.unistuttgart.ims.coref.annotator;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

import de.unistuttgart.ims.coref.annotator.api.v2.Mention;

public class MentionTransfer implements Transferable {
	public static DataFlavor dataFlavor = new DataFlavor(MentionTransfer.class, "Mention");

	CATreeNode treeNode;
	ImmutableList<Mention> mentions;

	public MentionTransfer(CATreeNode tn, Mention... mention) {
		this.treeNode = tn;
		this.mentions = Lists.immutable.of(mention);
	}

	public MentionTransfer(CATreeNode tn, Iterable<Mention> mentions) {
		this.treeNode = tn;
		this.mentions = Lists.immutable.ofAll(mentions);
	}

	public MentionTransfer(Iterable<Mention> mentions) {
		this.mentions = Lists.immutable.ofAll(mentions);
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
		return mentions;
	}

	public CATreeNode getTreeNode() {
		return treeNode;
	}
}
