package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.uima.UIMAException;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CompareMentionsWindow;
import de.unistuttgart.ims.coref.annotator.comp.SelectTwoFiles;
import de.unistuttgart.ims.coref.annotator.worker.JCasLoader;

public class FileCompareOpenAction extends AnnotatorAction {

	private static final long serialVersionUID = 1L;

	JDialog dialog;
	JTextField[] names = new JTextField[2];
	JLabel[] labels = new JLabel[2];

	public FileCompareOpenAction(Annotator mApp) {
		super(mApp, MaterialDesign.MDI_COMPARE);
		putValue(Action.NAME, Annotator.getString("action.compare"));
		init();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		dialog.setVisible(true);
		dialog.pack();
	}

	protected void init() {
		if (dialog == null) {
			dialog = initialiseDialog();
		}
	}

	protected JDialog initialiseDialog() {
		return new SelectTwoFiles(new RunComparisonAction());
	}

	class RunComparisonAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RunComparisonAction() {
			putValue(Action.NAME, "Compare");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			SelectTwoFiles stf = (SelectTwoFiles) SwingUtilities.getWindowAncestor((Component) e.getSource());
			CompareMentionsWindow cmw;
			try {
				cmw = new CompareMentionsWindow(mainApplication);
				new JCasLoader(jcas -> cmw.setJCasLeft(jcas, stf.getNames()[0]), stf.getFiles()[0]).execute();
				new JCasLoader(jcas -> cmw.setJCasRight(jcas, stf.getNames()[1]), stf.getFiles()[1]).execute();
				cmw.setVisible(true);
				cmw.pack();
				dialog.setVisible(false);
			} catch (UIMAException e1) {
				Annotator.logger.catching(e1);
			}
		}

	}

	class SelectFileAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		JPanel panel;
		int index;

		public SelectFileAction(JPanel panel, int index) {
			this.panel = panel;
			this.index = index;
			putValue(Action.NAME, "Select");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();

			chooser.setCurrentDirectory(new File("/Users/reiterns/Documents/CRETA/Code/coreference/annotations"));
			int r = chooser.showOpenDialog(null);
			if (r == JFileChooser.APPROVE_OPTION) {
				String filename = chooser.getSelectedFile().getName();
				// ((JButton) panel.getComponent(1)).setText(filename);
				((JTextField) panel.getComponent(0)).setText(filename);
				labels[index].setText(chooser.getSelectedFile().getAbsolutePath());
			}
		}

	}

}
