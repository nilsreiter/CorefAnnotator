package de.unistuttgart.ims.coref.annotator.action;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang.StringUtils;
import org.eclipse.collections.api.list.ImmutableList;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.FileFilters;
import de.unistuttgart.ims.coref.annotator.HelpWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.api.v1.Entity;
import de.unistuttgart.ims.coref.annotator.api.v1.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.v1.Flag;
import de.unistuttgart.ims.coref.annotator.api.v1.Mention;
import de.unistuttgart.ims.coref.annotator.document.FlagModel;

public class EntityStatisticsAction extends DocumentWindowAction {

	private static final String ENTITY_GROUP = "entityGroup";
	private static final String ENTITY_LABEL = "entityLabel";
	private static final String ENTITY_NUM = "entityNum";
	private static final String SURFACE = "surface";
	private static final String END = "end";
	private static final String BEGIN = "begin";
	private static final String CONTEXT_LEFT = "leftContext";
	private static final String CONTEXT_RIGHT = "rightContext";

	private static final long serialVersionUID = 1L;

	// initial option values
	int optionContextWidth = 10;
	boolean optionTrimWhitespace = true;

	public EntityStatisticsAction(DocumentWindow dw) {
		super(dw, Strings.ACTION_ENTITY_STATISTICS, MaterialDesign.MDI_CHART_BAR);
	}

	protected void optionDialog(Consumer<EntityStatisticsAction> callback) {

		JSpinner spinner = new JSpinner(new SpinnerNumberModel(optionContextWidth, 0, 100, 5));
		JCheckBox trimWhitespace = new JCheckBox();
		trimWhitespace.setSelected(optionTrimWhitespace);

		JDialog dialog = new JDialog(getTarget(), Annotator.getString(Strings.DIALOG_EXPORT_OPTIONS_TITLE));

		JPanel optionPanel = new JPanel(new GridLayout(0, 2));
		optionPanel.add(getLabel(Annotator.getString("dialog.export_options.context_width"),
				Annotator.getString("dialog.export_options.context_width.tooltip")));
		optionPanel.add(spinner);

		optionPanel.add(getLabel(Annotator.getString("dialog.export_options.trim_whitespace"),
				Annotator.getString("dialog.export_options.trim_whitespace.tooltip")));
		optionPanel.add(trimWhitespace);

		optionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		Action okAction = new AbstractAction(Annotator.getString(Strings.DIALOG_EXPORT_OPTIONS_OK)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				optionContextWidth = ((SpinnerNumberModel) spinner.getModel()).getNumber().intValue();
				optionTrimWhitespace = trimWhitespace.isSelected();
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
					FlagModel flagModel = getDocumentWindow().getDocumentModel().getFlagModel();
					ImmutableList<Flag> mentionFlags = flagModel.getFlags()
							.select(f -> f.getTargetClass().equalsIgnoreCase(Mention.class.getName()));
					ImmutableList<Flag> entityFlags = flagModel.getFlags()
							.select(f -> f.getTargetClass().equalsIgnoreCase(Entity.class.getName()));

					String text = getDocumentWindow().getDocumentModel().getJcas().getDocumentText();

					try (CSVPrinter p = new CSVPrinter(new FileWriter(chooser.getSelectedFile()), CSVFormat.EXCEL)) {
						// this is the header row
						p.print(BEGIN);
						p.print(END);
						if (optionContextWidth > 0) {
							p.print(CONTEXT_LEFT);
						}
						p.print(SURFACE);
						if (optionContextWidth > 0) {
							p.print(CONTEXT_RIGHT);
						}
						p.print(ENTITY_NUM);
						p.print(ENTITY_LABEL);
						p.print(ENTITY_GROUP);
						for (Flag flag : entityFlags) {
							p.print(Annotator.getString(flag.getLabel(), flag.getLabel()));
						}
						for (Flag flag : mentionFlags) {
							p.print(Annotator.getString(flag.getLabel(), flag.getLabel()));
						}
						p.println();
						int entityNum = 0;
						for (Entity entity : getDocumentWindow().getSelectedEntities()) {
							for (Mention mention : getDocumentWindow().getDocumentModel().getCoreferenceModel()
									.get(entity)) {
								String surface = mention.getCoveredText();
								if (mention.getDiscontinuous() != null)
									surface += " " + mention.getDiscontinuous().getCoveredText();
								p.print(mention.getBegin());
								p.print(mention.getEnd());
								if (optionContextWidth > 0) {
									String lc;
									if (optionTrimWhitespace) {
										lc = StringUtils.right(text.substring(0, mention.getBegin()).trim(),
												optionContextWidth);

									} else {
										lc = StringUtils.right(text, optionContextWidth);
									}
									p.print(lc);
								}
								p.print((optionTrimWhitespace ? surface.trim() : surface));
								if (optionContextWidth > 0) {
									String rc;
									if (optionTrimWhitespace) {
										rc = StringUtils.left(text.substring(mention.getEnd()).trim(),
												optionContextWidth);
									} else {
										rc = StringUtils.left(text, optionContextWidth);
									}
									p.print(rc);
								}
								p.print(entityNum);
								p.print(entity.getLabel());
								p.print((entity instanceof EntityGroup));
								for (Flag flag : entityFlags) {
									p.print(Util.isX(entity, flag.getKey()));
								}
								for (Flag flag : mentionFlags) {
									p.print(Util.isX(mention, flag.getKey()));
								}
								p.println();
							}
							entityNum++;
						}
					} catch (IOException e1) {
						Annotator.logger.catching(e1);
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
