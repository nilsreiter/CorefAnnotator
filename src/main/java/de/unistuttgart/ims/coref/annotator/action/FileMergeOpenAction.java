package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.comp.SelectTwoFiles;
import de.unistuttgart.ims.coref.annotator.plugins.MergeFilesPlugin;

public class FileMergeOpenAction extends IkonAction {

	private static final long serialVersionUID = 1L;

	public FileMergeOpenAction() {
		super(Constants.Strings.ACTION_FILE_MERGE, MaterialDesign.MDI_SOURCE_MERGE);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Constants.Strings.ACTION_FILE_MERGE_TOOLTIP));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JDialog dialog = new SelectTwoFiles(new RunMergeAction());
		dialog.setVisible(true);
		dialog.pack();
	}

	class RunMergeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RunMergeAction() {
			putValue(Action.NAME, "Merge");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			SelectTwoFiles stf = (SelectTwoFiles) SwingUtilities.getWindowAncestor((Component) e.getSource());

			MergeFilesPlugin pl = new MergeFilesPlugin();
			pl.setFiles(stf.getFiles().toImmutable());

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					DocumentWindow dw = Annotator.app.open(stf.getFiles().getFirst(), pl, "");
					dw.setFile(null);
				}

			});

		}

	}

}
