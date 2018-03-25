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
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;

public class SelectTwoFiles extends JDialog {

	private static final long serialVersionUID = 1L;

	MutableList<File> files = Lists.mutable.empty();
	MutableList<String> names = Lists.mutable.empty();
	Action finalAction;

	private JPanel getSelectPanel(int index) {
		JPanel panel = new JPanel();
		BoxLayout bl = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(bl);
		panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		JTextField textField = new JTextField();
		textField.setMaximumSize(new Dimension(200, 20));
		textField.setColumns(40);
		panel.add(textField);
		panel.add(new JButton(new SelectFileAction(panel, index)));
		panel.add(Box.createVerticalGlue());
		panel.setPreferredSize(new Dimension(200, 100));
		return panel;
	}

	public SelectTwoFiles(Action action) {
		super();
		this.finalAction = action;
		// JSplitPane splitPane = new JSplitPane();
		JPanel splitPane = new JPanel();
		splitPane.add(getSelectPanel(0));
		splitPane.add(getSelectPanel(1));

		JPanel moreLessPanel = new JPanel();
		BoxLayout bl = new BoxLayout(moreLessPanel, BoxLayout.Y_AXIS);
		moreLessPanel.setLayout(bl);

		JButton lessFilesButton = new JButton(Annotator.getString("less"));
		lessFilesButton.setIcon(FontIcon.of(MaterialDesign.MDI_MINUS_BOX));
		lessFilesButton.setEnabled(false);

		JButton moreFilesButton = new JButton(Annotator.getString("more"));
		moreFilesButton.setIcon(FontIcon.of(MaterialDesign.MDI_PLUS_BOX));
		moreFilesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				splitPane.add(getSelectPanel(splitPane.getComponentCount() - 2), splitPane.getComponentCount());
				splitPane.add(moreLessPanel);
				lessFilesButton.setEnabled(splitPane.getComponentCount() > 3);
				revalidate();
				pack();
			}
		});

		lessFilesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				splitPane.remove(splitPane.getComponentCount() - 2);
				lessFilesButton.setEnabled(splitPane.getComponentCount() > 3);
				files.remove(files.size() - 1);
				names.remove(names.size() - 1);
				revalidate();
				pack();
			}
		});
		moreLessPanel.add(moreFilesButton);
		moreLessPanel.add(lessFilesButton);
		splitPane.add(moreLessPanel);

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

			chooser.setCurrentDirectory(Annotator.app.getCurrentDirectory());
			int r = chooser.showOpenDialog(null);
			if (r == JFileChooser.APPROVE_OPTION) {
				String filename = chooser.getSelectedFile().getName();
				((JTextField) panel.getComponent(0)).setText(filename);
				files.add(chooser.getSelectedFile());
				names.add(files.get(index).getName());
				Annotator.app.setCurrentDirectory(chooser.getSelectedFile().getParentFile());
			}
		}

	}

	public File[] getFilesArray() {
		return files.toArray(new File[files.size()]);
	}

	public MutableList<File> getFiles() {
		return files;
	}

	public MutableList<String> getNames() {
		return names;
	}
}
