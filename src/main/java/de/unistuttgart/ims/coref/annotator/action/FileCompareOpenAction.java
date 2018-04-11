package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.apache.uima.UIMAException;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CompareMentionsWindow;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.comp.SelectTwoFiles;
import de.unistuttgart.ims.coref.annotator.worker.JCasLoader;

public class FileCompareOpenAction extends IkonAction {

	private static final long serialVersionUID = 1L;

	public FileCompareOpenAction() {
		super(Constants.Strings.ACTION_COMPARE, MaterialDesign.MDI_COMPARE);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Constants.Strings.ACTION_COMPARE_TOOLTIP));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JDialog dialog = new SelectTwoFiles(new RunComparisonAction());
		dialog.setVisible(true);
		dialog.pack();
	}

	class RunComparisonAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RunComparisonAction() {
			putValue(Action.NAME, "Compare");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			SelectTwoFiles stf = (SelectTwoFiles) SwingUtilities.getWindowAncestor((Component) e.getSource());

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					CompareMentionsWindow cmw;

					try {
						cmw = new CompareMentionsWindow(Annotator.app, stf.getFiles().size());
						cmw.setIndeterminateProgress();
						cmw.setVisible(true);
						cmw.setFiles(stf.getFiles());

						for (int i = 0; i < stf.getFiles().size(); i++) {
							final int j = i;
							new JCasLoader(jcas -> {
								cmw.setJCas(jcas, stf.getNames().get(j), j);
							}, stf.getFiles().get(i)).execute();

						}
						cmw.setVisible(true);
						cmw.pack();
						SwingUtilities.getWindowAncestor((Component) e.getSource()).setVisible(false);
					} catch (UIMAException e1) {
						Annotator.logger.catching(e1);
					}
				}

			});

		}

	}

}
