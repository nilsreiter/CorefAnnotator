package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.apache.uima.jcas.cas.TOP;

public class DetailsPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	DocumentWindow documentWindow;
	JTree tree;
	DefaultTreeModel treeModel;
	TreeNode<TOP> rootNode;

	public DetailsPanel(DocumentWindow dw) {
		super(new BorderLayout());
		documentWindow = dw;

		rootNode = new TreeNode<TOP>(null, "Add new entity");

		treeModel = new DefaultTreeModel(rootNode);

		tree = new JTree(treeModel);
		tree.setVisibleRowCount(-1);
		tree.setDropMode(DropMode.ON);
		// list.setCellRenderer(new CellRenderer());
		tree.setTransferHandler(new TransferHandler() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean canImport(TransferHandler.TransferSupport info) {
				// we only import Strings
				if (!info.isDataFlavorSupported(PotentialAnnotationTransfer.dataFlavor)) {
					return false;
				}

				JTree.DropLocation dl = (JTree.DropLocation) info.getDropLocation();
				if (dl.getPath() == null)
					return false;
				TreePath tp = dl.getPath();
				if (tp.getPathCount() > 2)
					return false;

				return true;
			}

			@Override
			public boolean importData(TransferHandler.TransferSupport info) {
				if (!info.isDrop()) {
					return false;
				}

				// Check for String flavor
				if (!info.isDataFlavorSupported(PotentialAnnotationTransfer.dataFlavor)) {
					displayDropLocation("List doesn't accept a drop of this type.");
					return false;
				}

				JTree.DropLocation dl = (JTree.DropLocation) info.getDropLocation();
				TreePath tp = dl.getPath();
				try {
					System.err.println(tp);
					((TreeNode<?>) tp.getLastPathComponent()).registerDrop(treeModel, (PotentialAnnotation) info
							.getTransferable().getTransferData(PotentialAnnotationTransfer.dataFlavor));
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
				return null;
			}

		});
		// listModel.addElement();

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

	class CellRenderer extends JLabel implements TreeCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			JLabel lab = new JLabel();

			lab.setText(value.toString());
			lab.setBackground(documentWindow.getColorMap().get(value));
			lab.setOpaque(true);
			return lab;
		}

	}

}
