package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.analyzer.AnalyzerWindow;
import de.unistuttgart.ims.coref.annotator.worker.DocumentModelLoader;
import de.unistuttgart.ims.coref.annotator.worker.JCasLoader;

public class FileSelectAnalyzeAction extends IkonAction {

	private static final long serialVersionUID = 1L;

	public FileSelectAnalyzeAction() {
		super(MaterialDesign.MDI_CHART_LINE);
		putValue(Action.NAME, Annotator.getString(Strings.ACTION_ANALYZER));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		AnalyzerWindow aw = new AnalyzerWindow();
		Annotator.app.fileOpenDialog(null, Annotator.app.getPluginManager().getDefaultIOPlugin(), false, file -> {
			JCasLoader loader = new JCasLoader(file[0], jcas -> {
				DocumentModelLoader dml = new DocumentModelLoader(dm -> {
					dm.setFile(file[0]);
					aw.setDocumentModel(dm);
				}, jcas);
				dml.execute();
			}, ex -> {
			});
			loader.execute();
		}, o -> Annotator.app.showOpening(), "");

	}
}
