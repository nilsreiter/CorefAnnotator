package de.unistuttgart.ims.coref.annotator.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.uima.UIMAException;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CompareMentionsWindow;
import de.unistuttgart.ims.coref.annotator.Constants;
import de.unistuttgart.ims.coref.annotator.comp.SelectTwoFiles;
import de.unistuttgart.ims.coref.annotator.worker.JCasLoader;

public class FileCompareOpenAction extends AnnotatorAction {

	private static final long serialVersionUID = 1L;

	JDialog dialog;
	JTextField[] names = new JTextField[2];
	JLabel[] labels = new JLabel[2];

	public FileCompareOpenAction(Annotator mApp) {
		super(mApp, MaterialDesign.MDI_COMPARE);
		putValue(Action.NAME, Annotator.getString(Constants.Strings.ACTION_COMPARE));
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Constants.Strings.ACTION_COMPARE_TOOLTIP));
		init();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		dialog.setVisible(true);
		dialog.pack();
	}

	protected void init() {
		if (dialog == null) {
			dialog = initialiseDialog();
		}
	}

	protected JDialog initialiseDialog() {
		return new SelectTwoFiles(new RunComparisonAction());
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
						cmw = new CompareMentionsWindow(mainApplication);
						cmw.setIndeterminateProgress();
						cmw.setVisible(true);
						cmw.setFileLeft(stf.getFiles()[0]);
						cmw.setFileRight(stf.getFiles()[1]);
						new JCasLoader(jcas -> {
							cmw.setJCasLeft(jcas, stf.getNames()[0]);
						}, stf.getFiles()[0]).execute();
						new JCasLoader(jcas -> {
							cmw.setJCasRight(jcas, stf.getNames()[1]);
						}, stf.getFiles()[1]).execute();
						cmw.setVisible(true);
						cmw.pack();
						dialog.setVisible(false);
					} catch (UIMAException e1) {
						Annotator.logger.catching(e1);
					}
				}

			});

		}

	}

}
