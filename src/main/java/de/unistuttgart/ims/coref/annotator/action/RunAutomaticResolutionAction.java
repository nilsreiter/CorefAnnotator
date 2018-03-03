package de.unistuttgart.ims.coref.annotator.action;

import java.awt.event.ActionEvent;

import org.apache.uima.jcas.JCas;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import de.unistuttgart.ims.coref.annotator.DocumentWindow;
import de.unistuttgart.ims.coref.annotator.plugins.AutomaticCRPlugin;
import de.unistuttgart.ims.coref.annotator.worker.RunPipeline;

public class RunAutomaticResolutionAction extends DocumentWindowAction implements RunPipeline.Callback {

	private static final long serialVersionUID = 1L;
	AutomaticCRPlugin plugin;

	public RunAutomaticResolutionAction(DocumentWindow dw, AutomaticCRPlugin plugin) {
		super(dw, plugin.getName(), false, MaterialDesign.MDI_HIGHWAY);
		this.plugin = plugin;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		documentWindow.setIndeterminateProgress();

		RunPipeline rp = new RunPipeline(documentWindow.getJcas(), plugin.getEngineDescription(), this);
		rp.execute();
	}

	@Override
	public void done(JCas jcas) {
		documentWindow.setJCas(jcas);
		documentWindow.stopIndeterminateProgress();
	}

}
