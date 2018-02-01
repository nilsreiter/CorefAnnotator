package de.unistuttgart.ims.coref.annotator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.apache.uima.fit.factory.AnnotationFactory;

import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class DetailsPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	DocumentWindow documentWindow;
	JList<DiscourseEntityEntry> list;
	DefaultListModel<DiscourseEntityEntry> listModel;

	public DetailsPanel(DocumentWindow dw) {
		super(new BorderLayout());
		documentWindow = dw;

		listModel = new DefaultListModel<DiscourseEntityEntry>();

		list = new JList<DiscourseEntityEntry>(listModel);
		list.setVisibleRowCount(-1);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setDropMode(DropMode.ON);
		list.setCellRenderer(new CellRenderer());
		list.setTransferHandler(new TransferHandler() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean canImport(TransferHandler.TransferSupport info) {
				// we only import Strings
				if (!info.isDataFlavorSupported(AnnotationTransfer.dataFlavor)) {
					return false;
				}

				JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
				if (dl.getIndex() == -1) {
					return false;
				}
				return true;
			}

			@Override
			public boolean importData(TransferHandler.TransferSupport info) {
				if (!info.isDrop()) {
					return false;
				}

				// Check for String flavor
				if (!info.isDataFlavorSupported(AnnotationTransfer.dataFlavor)) {
					displayDropLocation("List doesn't accept a drop of this type.");
					return false;
				}

				JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
				int index = dl.getIndex();
				try {
					listModel.get(index).registerDrop((PotentialAnnotation) info.getTransferable()
							.getTransferData(AnnotationTransfer.dataFlavor));
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
				return LINK;
			}

			@Override
			public Transferable createTransferable(JComponent comp) {
				return null;
			}

		});
		listModel.addElement(new DiscourseEntityEntry(null, "Add new entity") {
			@Override
			public void registerDrop(PotentialAnnotation anno) {
				System.err.println("new entity received");
				Entity e = new Entity(anno.getTextView().getJCas());
				e.addToIndexes();
				String s = anno.getTextView().getJCas().getDocumentText().substring(anno.getBegin(), anno.getEnd());
				listModel.addElement(new DiscourseEntityEntry(e, s));
				System.err.println(" added to model");

				Mention m = AnnotationFactory.createAnnotation(anno.getTextView().getJCas(), anno.getBegin(),
						anno.getEnd(), Mention.class);
				m.setEntity(e);
			}

		});

		this.add(list, BorderLayout.CENTER);
	}

	private void displayDropLocation(final String string) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, string);
			}
		});
	}

	class CellRenderer extends JLabel implements ListCellRenderer<DiscourseEntityEntry> {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList<? extends DiscourseEntityEntry> list,
				DiscourseEntityEntry value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel lab = new JLabel();
			lab.setText(value.getLabel());
			lab.setBackground(documentWindow.getColorMap().get(value.getJcasRepresentation()));
			lab.setOpaque(true);
			return lab;
		}

	}

}
