package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.function.Consumer;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.FileFilters;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.plugin.csv.CSVWriter;
import de.unistuttgart.ims.coref.annotator.plugin.csv.Constants;
import de.unistuttgart.ims.coref.annotator.plugin.csv.Defaults;
import de.unistuttgart.ims.coref.annotator.plugin.csv.Plugin;
import de.unistuttgart.ims.coref.annotator.plugin.csv.Plugin.ContextUnit;

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

		Plugin csvPlugin = Annotator.app.getPluginManager().getPlugin(Plugin.class);

		csvPlugin.showExportConfigurationDialog(getTarget(), getTarget().getDocumentModel(), p -> {
			callback.accept(EntityStatisticsAction.this);
		});

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
					csvWriter.setOptionContextWidth(Annotator.app.getPreferences()
							.getInt(Constants.PLUGIN_CSV_CONTEXT_WIDTH, Defaults.CFG_OPTION_CONTEXT_WIDTH));
					csvWriter.setOptionReplaceNewlines(Annotator.app.getPreferences()
							.getBoolean(Constants.PLUGIN_CSV_REPLACE_NEWLINES, Defaults.CFG_OPTION_REPLACE_NEWLINES));
					csvWriter.setOptionTrimWhitespace(Annotator.app.getPreferences()
							.getBoolean(Constants.PLUGIN_CSV_TRIM, Defaults.CFG_OPTION_TRIM));
					csvWriter.setOptionContextUnit(ContextUnit.valueOf(Annotator.app.getPreferences()
							.get(Constants.PLUGIN_CSV_CONTEXT_UNIT, Defaults.CFG_OPTION_CONTEXT_UNIT.name())));

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
