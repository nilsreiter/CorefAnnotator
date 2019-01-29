package de.unistuttgart.ims.coref.annotator.comp;

import java.util.EventListener;

import javax.swing.event.TreeModelEvent;

public interface SortingTreeModelListener extends EventListener {

	public void treeNodesPreResort(TreeModelEvent e);

	public void treeNodesPostResort(TreeModelEvent e);
}
