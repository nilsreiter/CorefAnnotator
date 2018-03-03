package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.Annotator;
import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.plugins.AutomaticCRPlugin;

public class RunAutomaticResolutionAction extends DocumentWindowAction {

	private static final long serialVersionUID = 1L;
	AutomaticCRPlugin plugin;

	public RunAutomaticResolutionAction(DocumentWindow dw, AutomaticCRPlugin plugin) {
		super(dw, plugin.getName(), false, MaterialDesign.MDI_HIGHWAY);
		this.plugin = plugin;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		documentWindow.setIndeterminateProgress();
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					JCas jcas = documentWindow.getJcas();
					SimplePipeline.runPipeline(jcas, plugin.getEngineDescription());
					documentWindow.setJCas(jcas);
					documentWindow.stopIndeterminateProgress();
				} catch (AnalysisEngineProcessException | ResourceInitializationException e1) {
					Annotator.logger.catching(e1);
				}
			}

		});
	}

}
