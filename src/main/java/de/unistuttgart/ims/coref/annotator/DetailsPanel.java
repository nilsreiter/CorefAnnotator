package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.uima.jcas.cas.TOP;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class DetailsPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	DocumentWindow documentWindow;
	JTree tree;
	DefaultTreeModel treeModel;
	TreeNode<TOP> rootNode;
	Map<Long, Mention> mentionCache;

	public DetailsPanel(DocumentWindow dw) {
		super(new BorderLayout());
		documentWindow = dw;

		rootNode = new TreeNode<TOP>(null, "Add new entity");

		treeModel = new DefaultTreeModel(rootNode);

		tree = new JTree(treeModel);
		tree.setVisibleRowCount(-1);
		tree.setDragEnabled(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setTransferHandler(new TransferHandler() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean canImport(TransferHandler.TransferSupport info) {
				JTree.DropLocation dl = (JTree.DropLocation) info.getDropLocation();
				if (dl.getPath() == null)
					return false;

				if (info.isDataFlavorSupported(PotentialAnnotationTransfer.dataFlavor)) {
					if (dl.getPath().getPathCount() > 2)
						return false;
				}
				if (info.isDataFlavorSupported(AnnotationTransfer.dataFlavor)) {
					if (dl.getPath().getPathCount() > 2)
						return false;
				}

				return true;
			}

			@SuppressWarnings("unchecked")
			@Override
			public boolean importData(TransferHandler.TransferSupport info) {
				if (!info.isDrop()) {
					return false;
				}

				// Check for String flavor
				if (!info.isDataFlavorSupported(PotentialAnnotationTransfer.dataFlavor)
						&& !info.isDataFlavorSupported(NodeTransferable.dataFlavor)) {
					displayDropLocation("List doesn't accept a drop of this type.");
					return false;
				}

				JTree.DropLocation dl = (JTree.DropLocation) info.getDropLocation();
				TreePath tp = dl.getPath();
				tree.expandPath(tp);

				try {
					if (info.getTransferable().getTransferDataFlavors()[0] == PotentialAnnotationTransfer.dataFlavor) {
						((TreeNode<?>) tp.getLastPathComponent()).registerDrop(treeModel, (PotentialAnnotation) info
								.getTransferable().getTransferData(PotentialAnnotationTransfer.dataFlavor));

					} else if (info.getTransferable().getTransferDataFlavors()[0] == NodeTransferable.dataFlavor) {
						((TreeNode<?>) tp.getLastPathComponent()).registerDrop(treeModel, documentWindow,
								(TreeNode<Mention>) info.getTransferable()
										.getTransferData(NodeTransferable.dataFlavor));
					}

				} catch (UnsupportedFlavorException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

				return true;
			}

			@Override
			public int getSourceActions(JComponent c) {
				return MOVE;
			}

			@Override
			public Transferable createTransferable(JComponent comp) {
				JTree tree = (JTree) comp;
				@SuppressWarnings("unchecked")
				TreeNode<Mention> tn = (TreeNode<Mention>) tree.getLastSelectedPathComponent();
				return new NodeTransferable<Mention>(tn);
			}

			@Override
			protected void exportDone(JComponent c, Transferable t, int action) {
				try {
					treeModel.removeNodeFromParent((MutableTreeNode) t.getTransferData(null));
				} catch (UnsupportedFlavorException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		// listModel.addElement();
		tree.setCellRenderer(new CellRenderer());
		this.add(tree, BorderLayout.CENTER);
	}

	private void displayDropLocation(final String string) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, string);
			}
		});
	}

	class CellRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			JLabel s = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row,
					hasFocus);
			if (value instanceof TreeNode && ((TreeNode) value).getFeatureStructure() instanceof Entity) {
				Entity e = (Entity) ((TreeNode) value).getFeatureStructure();

				if (documentWindow.getColorMap().containsKey(e)) {
					s.setBackground(documentWindow.getColorMap().get(e));
					s.setOpaque(true);
				} else {
					s.setOpaque(false);
				}
			} else {
				s.setOpaque(false);
			}

			return s;
		}

	}

}
