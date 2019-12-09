package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.comp.SelectTwoFiles;
import de.unistuttgart.ims.coref.annotator.uima.MergeFilesPlugin;

public class FileMergeOpenAction extends IkonAction {

	private static final long serialVersionUID = 1L;

	public FileMergeOpenAction() {
		super(Strings.ACTION_FILE_MERGE, MaterialDesign.MDI_SOURCE_MERGE);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_FILE_MERGE_TOOLTIP));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Annotator.app.fileOpenDialog(null, Annotator.app.getPluginManager().getDefaultIOPlugin(), true, files -> {
			ImmutableList<File> fileList = Lists.immutable.of(files);
			MergeFilesPlugin pl = new MergeFilesPlugin();
			pl.setFiles(fileList);

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					DocumentWindow dw = Annotator.app.open(fileList.getFirst(), pl, "");
					dw.setFile(null);
				}

			});
		}, o -> {
		}, "");
	}

	@Deprecated
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
