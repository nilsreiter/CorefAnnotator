package de.unistuttgart.ims.coref.annotator.action;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.FileFilters;
import de.unistuttgart.ims.coref.annotator.HelpWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.plugin.csv.CSVWriter;

public class EntityStatisticsAction extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	// initial option values
	int optionContextWidth = 0;
	boolean optionTrimWhitespace = true;
	boolean optionReplaceNewlines = true;

	public EntityStatisticsAction(DocumentWindow dw) {
		super(dw, Strings.ACTION_ENTITY_STATISTICS, MaterialDesign.MDI_CHART_BAR);
	}

	protected void optionDialog(Consumer<EntityStatisticsAction> callback) {

		JSpinner spinner = new JSpinner(new SpinnerNumberModel(optionContextWidth, 0, 500, 25));
		JCheckBox trimWhitespace = new JCheckBox();
		JCheckBox replaceNewlineCharacters = new JCheckBox();
		trimWhitespace.setSelected(optionTrimWhitespace);

		JDialog dialog = new JDialog(getTarget(), Annotator.getString(Strings.DIALOG_EXPORT_OPTIONS_TITLE));

		JPanel optionPanel = new JPanel(new GridLayout(0, 2));
		optionPanel.add(getLabel(Annotator.getString("dialog.export_options.context_width"),
				Annotator.getString("dialog.export_options.context_width.tooltip")));
		optionPanel.add(spinner);

		optionPanel.add(getLabel(Annotator.getString("dialog.export_options.trim_whitespace"),
				Annotator.getString("dialog.export_options.trim_whitespace.tooltip")));
		optionPanel.add(trimWhitespace);

		optionPanel.add(getLabel(Annotator.getString("dialog.export_options.replace_newline"),
				Annotator.getString("dialog.export_options.replace_newline.tooltip")));
		optionPanel.add(replaceNewlineCharacters);

		optionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		Action okAction = new AbstractAction(Annotator.getString(Strings.DIALOG_EXPORT_OPTIONS_OK)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				optionContextWidth = ((SpinnerNumberModel) spinner.getModel()).getNumber().intValue();
				optionTrimWhitespace = trimWhitespace.isSelected();
				optionReplaceNewlines = replaceNewlineCharacters.isSelected();
				dialog.dispose();
				callback.accept(EntityStatisticsAction.this);
			}
		};

		Action cancelAction = new AbstractAction(
				Annotator.getString(de.unistuttgart.ims.coref.annotator.Strings.DIALOG_CANCEL)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		};

		Action helpAction = new AbstractAction(
				Annotator.getString(de.unistuttgart.ims.coref.annotator.Strings.MENU_HELP)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				HelpWindow.show("Input/Output");
			}
		};

		JButton okButton = new JButton(okAction);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.add(new JButton(cancelAction));
		buttonPanel.add(new JButton(helpAction));

		dialog.getContentPane().add(optionPanel, BorderLayout.CENTER);
		dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		dialog.pack();
		dialog.setLocationRelativeTo(getTarget());
		dialog.setVisible(true);
		SwingUtilities.getRootPane(okButton).setDefaultButton(okButton);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	protected void saveDialog() {
		JFileChooser chooser = new JFileChooser(Annotator.app.getCurrentDirectory());
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setFileFilter(FileFilters.csv);
		chooser.setDialogTitle(Annotator.getString(Strings.DIALOG_SAVE_AS_TITLE));

		String name = getDocumentWindow().getSelectedEntities().iterator().next().getLabel();
		if (name != null)
			chooser.setSelectedFile(new File(name + ".csv"));

		int r = chooser.showSaveDialog(getDocumentWindow());
		if (r == JFileChooser.APPROVE_OPTION) {
			new SwingWorker<Object, Object>() {

				@Override
				protected Object doInBackground() throws Exception {

					getDocumentWindow().setMessage(Annotator.getString(Strings.ENTITY_STATISTICS_STATUS));
					getDocumentWindow().setIndeterminateProgress();

					CSVWriter csvWriter = new CSVWriter();
					csvWriter.setEntities(getDocumentWindow().getSelectedEntities());
					csvWriter.setOptionContextWidth(optionContextWidth);
					csvWriter.setOptionReplaceNewlines(optionReplaceNewlines);
					csvWriter.setOptionTrimWhitespace(optionTrimWhitespace);

					try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
						csvWriter.write(getDocumentWindow().getDocumentModel().getJcas(), fw);
					}

					return null;
				}

				@Override
				protected void done() {
					getDocumentWindow().setMessage("");
					getDocumentWindow().stopIndeterminateProgress();
				}
			}.execute();

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		optionDialog(a -> a.saveDialog());
	}

	protected JLabel getLabel(String text, String tooltip) {
		JLabel lab = new JLabel(text);
		lab.setToolTipText(tooltip);
		return lab;
	}

}
