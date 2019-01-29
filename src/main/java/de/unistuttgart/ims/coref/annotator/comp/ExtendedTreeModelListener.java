package de.unistuttgart.ims.coref.annotator.comp;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public interface ExtendedTreeModelListener extends TreeModelListener {

	public void treeNodesPreResort(TreeModelEvent e);

	public void treeNodesPostResort(TreeModelEvent e);
}
