package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.tools.cvd.ColorIcon;

import de.unistuttgart.ims.coref.annotator.action.ChangeColorForEntity;
import de.unistuttgart.ims.coref.annotator.action.ChangeKeyForEntityAction;
import de.unistuttgart.ims.coref.annotator.action.RenameEntityAction;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class DetailsPanel extends JPanel implements TreeSelectionListener, TreeModelListener, LoadingListener {
	private static final long serialVersionUID = 1L;

	DocumentWindow documentWindow;
	JTree tree;
	@Deprecated
	CoreferenceModel treeModel;
	@Deprecated
	TreeNode<TOP> rootNode;
	Map<Long, Mention> mentionCache;

	AbstractAction renameAction;
	AbstractAction changeKeyAction;
	AbstractAction changeColorAction;

	JButton renameActionButton;
	JButton changeKeyActionButton;
	JButton changeColorActionButton;

	public DetailsPanel(DocumentWindow dw) {
		super(new BorderLayout());
		documentWindow = dw;

		this.initialiseUi();

	}

	protected void initialiseUi() {
		tree = new JTree();
		tree.getSelectionModel().addTreeSelectionListener(this);
		tree.setVisibleRowCount(-1);
		tree.setDragEnabled(true);
		tree.setLargeModel(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		tree.setTransferHandler(new PanelTransferHandler());
		tree.setCellRenderer(new CellRenderer());

		add(new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
				BorderLayout.CENTER);

		JPanel controls = new JPanel();

		renameActionButton = new JButton("rename");
		changeKeyActionButton = new JButton("change key");
		changeColorActionButton = new JButton("rename");

		controls.add(renameActionButton);
		controls.add(changeKeyActionButton);
		controls.add(changeColorActionButton);
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
				s.setIcon(new ColorIcon(new Color(e.getColor())));
				if (etn.getKeyCode() != null) {
					s.setText(etn.getKeyCode() + ": " + s.getText() + " (" + etn.getChildCount() + ")");
				} else
					s.setText(s.getText() + " (" + etn.getChildCount() + ")");
			} else {
				s.setOpaque(false);
			}

			return s;
		}

	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (e.getNewLeadSelectionPath() != null) {
			renameAction.setEnabled(e.getNewLeadSelectionPath().getPathCount() == 2);
			changeKeyAction.setEnabled(e.getNewLeadSelectionPath().getPathCount() == 2);
			changeColorAction.setEnabled(e.getNewLeadSelectionPath().getPathCount() == 2);
		}
	}

	@Override
	public void treeNodesInserted(TreeModelEvent e) {
		tree.expandPath(e.getTreePath());
	}

	@Override
	public void treeNodesChanged(TreeModelEvent e) {
		tree.expandPath(e.getTreePath());

	}

	@Override
	public void treeNodesRemoved(TreeModelEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void treeStructureChanged(TreeModelEvent e) {
		tree.expandPath(e.getTreePath());

	}

	@Override
	public void jcasLoaded(JCas jcas) {

	}

	@Override
	public void modelCreated(CoreferenceModel model) {
		model.addTreeModelListener(this);

		renameAction = new RenameEntityAction(model, tree);
		renameAction.setEnabled(false);
		renameActionButton.setAction(renameAction);

		changeKeyAction = new ChangeKeyForEntityAction(model, tree);
		changeKeyAction.setEnabled(false);
		changeKeyActionButton.setAction(changeKeyAction);

		changeColorAction = new ChangeColorForEntity(model, tree);
		changeColorAction.setEnabled(false);
		changeColorActionButton.setAction(changeColorAction);

		tree.setModel(model);

	}

	class PanelTransferHandler extends TransferHandler {

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
						((CoreferenceModel) tree.getModel()).addNewEntityMention(pa.getBegin(), pa.getEnd());
					else
						((CoreferenceModel) tree.getModel()).addNewMention((Entity) entity, pa.getBegin(), pa.getEnd());

				} else if (info.getTransferable().getTransferDataFlavors()[0] == NodeTransferable.dataFlavor) {
					FeatureStructure entity = ((TreeNode<?>) tp.getLastPathComponent()).getFeatureStructure();

					TreeNode<Mention> m = (TreeNode<Mention>) info.getTransferable()
							.getTransferData(NodeTransferable.dataFlavor);
					((CoreferenceModel) tree.getModel()).updateMention(m.getFeatureStructure(), (Entity) entity);

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

	}
}
