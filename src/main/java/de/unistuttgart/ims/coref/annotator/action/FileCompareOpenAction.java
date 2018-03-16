package de.unistuttgart.ims.coref.annotator.action;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import org.apache.uima.UIMAException;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CompareMentionsWindow;
import de.unistuttgart.ims.coref.annotator.worker.JCasLoader;

public class FileCompareOpenAction extends AnnotatorAction {

	private static final long serialVersionUID = 1L;

	JDialog dialog;
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
		JDialog dialog = new JDialog();
		JSplitPane splitPane = new JSplitPane();

		JPanel panel = new JPanel();
		BoxLayout bl = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(bl);
		panel.setBorder(BorderFactory.createTitledBorder("left"));
		panel.add(new JTextField());
		panel.add(new JButton(new SelectFileAction(panel, 0)));
		labels[0] = new JLabel();
		labels[0].setVisible(false);
		panel.add(labels[0]);
		splitPane.setLeftComponent(panel);

		panel = new JPanel();
		bl = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(bl);
		panel.setBorder(BorderFactory.createTitledBorder("right"));
		panel.add(new JTextField());
		panel.add(new JButton(new SelectFileAction(panel, 1)));
		labels[1] = new JLabel();
		labels[1].setVisible(false);
		panel.add(labels[1]);
		panel.getComponent(2).setVisible(false);
		splitPane.setRightComponent(panel);

		dialog.add(splitPane, BorderLayout.CENTER);
		dialog.add(new JButton(new RunComparisonAction()), BorderLayout.SOUTH);
		dialog.pack();
		return dialog;
	}

	class RunComparisonAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RunComparisonAction() {
			putValue(Action.NAME, "Compare");
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			CompareMentionsWindow cmw;
			try {
				cmw = new CompareMentionsWindow(mainApplication);

				new JCasLoader(jcas -> cmw.setJCasLeft(jcas), new File(labels[0].getText())).execute();

				new JCasLoader(jcas -> cmw.setJCasRight(jcas), new File(labels[1].getText())).execute();

				cmw.setVisible(true);
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
