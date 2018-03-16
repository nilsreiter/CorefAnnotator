package de.unistuttgart.ims.coref.annotator.action;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

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

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;

public class FileCompareOpenAction extends AnnotatorAction {

	private static final long serialVersionUID = 1L;

	JDialog dialog;

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
		panel.add(new JButton(new SelectFileAction(panel)));
		panel.add(new JLabel());
		panel.getComponent(2).setVisible(false);
		splitPane.setLeftComponent(panel);

		panel = new JPanel();
		bl = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(bl);
		panel.setBorder(BorderFactory.createTitledBorder("right"));
		panel.add(new JTextField());
		panel.add(new JButton(new SelectFileAction(panel)));
		panel.add(new JLabel());
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

		}

	}

	class SelectFileAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		JPanel panel;

		public SelectFileAction(JPanel panel) {
			this.panel = panel;
			putValue(Action.NAME, "Select");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();

			int r = chooser.showOpenDialog(null);
			if (r == JFileChooser.APPROVE_OPTION) {
				String filename = chooser.getSelectedFile().getName();
				// ((JButton) panel.getComponent(1)).setText(filename);
				((JTextField) panel.getComponent(0)).setText(filename);
				((JLabel) panel.getComponent(2)).setText(chooser.getSelectedFile().getAbsolutePath());
			}
		}

	}

}
