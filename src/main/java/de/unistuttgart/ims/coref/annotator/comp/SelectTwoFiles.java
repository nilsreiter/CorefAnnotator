package de.unistuttgart.ims.coref.annotator.comp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;

public class SelectTwoFiles extends JDialog {

	private static final long serialVersionUID = 1L;

	JTextField[] names = new JTextField[2];
	File[] files = new File[2];
	Action finalAction;

	public SelectTwoFiles(Action action) {
		super();
		this.finalAction = action;
		JSplitPane splitPane = new JSplitPane();

		JPanel panel = new JPanel();
		BoxLayout bl = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(bl);
		panel.setBorder(BorderFactory.createTitledBorder("left"));
		names[0] = new JTextField();
		names[0].setMaximumSize(new Dimension(200, 20));
		names[0].setColumns(40);
		panel.add(names[0]);
		panel.add(new JButton(new SelectFileAction(panel, 0)));
		panel.add(Box.createVerticalGlue());
		panel.setPreferredSize(new Dimension(200, 100));
		splitPane.setLeftComponent(panel);

		panel = new JPanel();
		bl = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(bl);
		panel.setBorder(BorderFactory.createTitledBorder("right"));
		names[1] = new JTextField();
		names[1].setMaximumSize(new Dimension(200, 20));
		names[1].setColumns(40);
		panel.add(names[1]);
		panel.add(new JButton(new SelectFileAction(panel, 1)));
		panel.add(Box.createVerticalGlue());
		panel.setPreferredSize(new Dimension(200, 80));
		splitPane.setRightComponent(panel);

		add(splitPane, BorderLayout.CENTER);

		JButton cancelButton = new JButton();
		cancelButton.setText(Annotator.getString(Constants.Strings.DIALOG_CANCEL));
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SelectTwoFiles.this.setVisible(false);
			}
		});

		JPanel buttonBar = new JPanel();
		buttonBar.add(new JButton(finalAction));
		buttonBar.add(cancelButton);
		add(buttonBar, BorderLayout.SOUTH);
		pack();
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
				((JTextField) panel.getComponent(0)).setText(filename);
				files[index] = chooser.getSelectedFile();
			}
		}

	}

	public File[] getFiles() {
		return files;
	}

	public String[] getNames() {
		return new String[] { names[0].getText(), names[1].getText() };
	}
}
