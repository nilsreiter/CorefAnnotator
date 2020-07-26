package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.SwingUtilities;

import org.apache.uima.UIMAException;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.CompareMentionsWindow;
import de.unistuttgart.ims.coref.annotator.CompareMentionsWindow.NotComparableException;
import de.unistuttgart.ims.coref.annotator.Strings;
import de.unistuttgart.ims.coref.annotator.worker.JCasLoader;

public class FileCompareOpenAction extends IkonAction {

	private static final long serialVersionUID = 1L;

	public FileCompareOpenAction() {
		super(Strings.ACTION_COMPARE, MaterialDesign.MDI_COMPARE);
		putValue(Action.SHORT_DESCRIPTION, Annotator.getString(Strings.ACTION_COMPARE_TOOLTIP));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Annotator.app.fileOpenDialog(null, Annotator.app.getPluginManager().getDefaultIOPlugin(), true, f -> {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					ImmutableList<File> files = Lists.immutable.of(f);

					CompareMentionsWindow cmw;
					try {
						cmw = new CompareMentionsWindow(Annotator.app, f.length);
						cmw.setIndeterminateProgress();
						cmw.setVisible(true);
						cmw.setFiles(files);

						for (int i = 0; i < f.length; i++) {
							final int j = i;

							new JCasLoader(f[i], jcas -> {
								try {
									cmw.setJCas(jcas, files.collect(file -> file.getName()).get(j), j);
								} catch (NotComparableException e) {
									Annotator.logger.catching(e);
									cmw.setVisible(false);
									cmw.dispose();
									Annotator.app.warnDialog(e.getLocalizedMessage(), "Loading Error");
								}
							}, ex -> {
								cmw.setVisible(false);
								cmw.dispose();
								Annotator.app.warnDialog(ex.getLocalizedMessage(), "Loading Error");
							}).execute();

						}
						cmw.setVisible(true);
						cmw.pack();
					} catch (UIMAException e1) {
						Annotator.logger.catching(e1);
					}
				}

			});
		}, o -> {
		}, "");
	}

}
