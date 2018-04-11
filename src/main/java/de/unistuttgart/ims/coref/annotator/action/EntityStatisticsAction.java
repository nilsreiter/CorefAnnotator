package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.SwingWorker;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.Constants.Strings;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.FileFilters;
import de.unistuttgart.ims.coref.annotator.Util;
import de.unistuttgart.ims.coref.annotator.api.Entity;
import de.unistuttgart.ims.coref.annotator.api.EntityGroup;
import de.unistuttgart.ims.coref.annotator.api.Mention;

public class EntityStatisticsAction extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;

	public EntityStatisticsAction(DocumentWindow dw) {
		super(dw, Constants.Strings.ACTION_ENTITY_STATISTICS, MaterialDesign.MDI_CHART_BAR);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
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
					getDocumentWindow().setMessage("Exporting ...");
					getDocumentWindow().setIndeterminateProgress();
					try (CSVPrinter p = new CSVPrinter(new FileWriter(chooser.getSelectedFile()), CSVFormat.EXCEL)) {
						p.printRecord("begin", "end", "surface", "entityNum", "entityLabel", "entityGroup",
								"entityGeneric", "ambiguous", "difficult");
						int entityNum = 0;
						for (Entity entity : getDocumentWindow().getSelectedEntities()) {
							for (Mention mention : getDocumentWindow().getDocumentModel().getCoreferenceModel()
									.get(entity)) {
								String surface = mention.getCoveredText();
								if (mention.getDiscontinuous() != null)
									surface += " " + mention.getDiscontinuous().getCoveredText();
								p.printRecord(mention.getBegin(), mention.getEnd(), surface, entityNum,
										entity.getLabel(), (entity instanceof EntityGroup), Util.isGeneric(entity),
										Util.isAmbiguous(mention), Util.isDifficult(mention));
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

}
