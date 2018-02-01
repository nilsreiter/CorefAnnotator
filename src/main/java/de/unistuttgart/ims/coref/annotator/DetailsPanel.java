package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.cas.TOP;

import de.unistuttgart.ims.coref.annotator.action.ChangeKeyForEntityAction;
import de.unistuttgart.ims.coref.annotator.action.RenameEntityAction;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class DetailsPanel extends JPanel implements TreeSelectionListener {
	private static final long serialVersionUID = 1L;

	DocumentWindow documentWindow;
	JTree tree;
	CoreferenceModel treeModel;
	TreeNode<TOP> rootNode;
	Map<Long, Mention> mentionCache;

	AbstractAction renameAction;
	AbstractAction changeKeyAction;

	public DetailsPanel(DocumentWindow dw, CoreferenceModel cm) {
		super(new BorderLayout());
		documentWindow = dw;

		rootNode = new TreeNode<TOP>(null, "Add new entity");

		treeModel = cm;

		tree = new JTree(treeModel);
		tree.getSelectionModel().addTreeSelectionListener(this);
		tree.setVisibleRowCount(-1);
		tree.setDragEnabled(true);
		tree.setPreferredSize(new Dimension(200, 600));
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
						FeatureStructure entity = ((TreeNode<?>) tp.getLastPathComponent()).getFeatureStructure();
						PotentialAnnotation pa = (PotentialAnnotation) info.getTransferable()
								.getTransferData(PotentialAnnotationTransfer.dataFlavor);
						if (entity == null)
							treeModel.addEntityMention(pa.getBegin(), pa.getEnd());
						else
							treeModel.addLink((Entity) entity, pa.getBegin(), pa.getEnd());

					} else if (info.getTransferable().getTransferDataFlavors()[0] == NodeTransferable.dataFlavor) {
						FeatureStructure entity = ((TreeNode<?>) tp.getLastPathComponent()).getFeatureStructure();

						TreeNode<Mention> m = (TreeNode<Mention>) info.getTransferable()
								.getTransferData(NodeTransferable.dataFlavor);
						treeModel.updateMention(m.getFeatureStructure(), (Entity) entity);

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

		});
		// listModel.addElement();
		tree.setCellRenderer(new CellRenderer());
		this.add(tree, BorderLayout.CENTER);

		JPanel controls = new JPanel();

		renameAction = new RenameEntityAction(treeModel, tree);
		renameAction.setEnabled(false);

		changeKeyAction = new ChangeKeyForEntityAction(treeModel, tree);
		changeKeyAction.setEnabled(false);

		controls.add(new JButton(renameAction));
		controls.add(new JButton(changeKeyAction));
		this.add(controls, BorderLayout.NORTH);

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
			if (value instanceof EntityTreeNode) {
				EntityTreeNode etn = (EntityTreeNode) value;
				Entity e = etn.getFeatureStructure();
				s.setIcon(treeModel.getIcon(e));

				if (treeModel.getColorMap().containsKey(e)) {
					// s.setBackground(treeModel.getColorMap().get(e));
					s.setOpaque(false);
					s.setText(s.getText() + " (" + (etn.getKeyCode()) + ")");

				} else {
					s.setOpaque(false);
				}
			} else {
				s.setOpaque(false);
			}

			return s;
		}

	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		renameAction.setEnabled(e.getNewLeadSelectionPath().getPathCount() == 2);
		changeKeyAction.setEnabled(e.getNewLeadSelectionPath().getPathCount() == 2);
	}

}
