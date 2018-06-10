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
import de.unistuttgart.ims.coref.annotator.uima.MergeFilesPlugin;

public class FileConnectOpenAction extends IkonAction {

	private static final long serialVersionUID = 1L;

	public FileConnectOpenAction() {
		super(Constants.Strings.ACTION_FILE_CONNECT, MaterialDesign.MDI_SOURCE_MERGE);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Constants.Strings.ACTION_FILE_CONNECT_TOOLTIP));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JDialog dialog = new SelectTwoFiles(new RunConnectAction());
		dialog.setVisible(true);
		dialog.pack();
	}

	class RunConnectAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RunConnectAction() {
			putValue(Action.NAME, "Connect");
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
